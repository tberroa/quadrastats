import random
import string
from cassiopeia.type.api.exception import APIError
from datetime import datetime
from django.contrib.auth import hashers
from django.core.mail import EmailMessage
from django.db.utils import IntegrityError
from django.http import HttpResponse
from portal.errors import FRIEND_ALREADY_LISTED
from portal.errors import FRIEND_EQUALS_USER
from portal.errors import FRIEND_LIMIT_REACHED
from portal.errors import INVALID_CREDENTIALS
from portal.errors import INVALID_REQUEST_FORMAT
from portal.errors import INVALID_RIOT_RESPONSE
from portal.errors import RUNE_PAGE_CODE_NOT_FOUND
from portal.errors import SUMMONER_ALREADY_REGISTERED
from portal.errors import SUMMONER_DOES_NOT_EXIST
from portal.errors import SUMMONER_NOT_IN_DATABASE
from portal.errors import SUMMONER_NOT_RANKED
from portal.errors import SUMMONER_NOT_REGISTERED
from portal.riot import format_key
from portal.riot import riot_request
from summoners.models import Summoner
from summoners.models import User
from summoners.serializers import summoner_serializer


def add_friend(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    user_key = data.get("user_key")
    friend_key = data.get("friend_key")

    # ensure the data is valid
    if None in (region, user_key, friend_key):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    user_key = format_key(user_key)
    friend_key = format_key(friend_key)

    # make sure friend is not the user
    if user_key == friend_key:
        return HttpResponse(FRIEND_EQUALS_USER)

    try:
        # get the users summoner object
        user_o = Summoner.objects.get(region=region, key=user_key)
        Summoner.objects.filter(pk=user_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # check if user is at friend limit or if friend is already listed
    if user_o.friends is not None:
        friends = user_o.friends.split(",")
        if len(friends) >= 20:
            return HttpResponse(FRIEND_LIMIT_REACHED)
        for friend in friends:
            if friend == friend_key:
                return HttpResponse(FRIEND_ALREADY_LISTED)

    try:
        # get the friends summoner object
        friend_o = Summoner.objects.get(region=region, key=friend_key)
        Summoner.objects.filter(pk=friend_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        try:
            # summoner not in database, request summoner data from riot
            args = {"request": 1, "key": friend_key}
            riot_response = riot_request(region, args)
        except APIError as e:
            if e.error_code == 404:
                return HttpResponse(SUMMONER_DOES_NOT_EXIST)
            else:
                return HttpResponse(INVALID_RIOT_RESPONSE)

        try:
            # extract the summoner
            friend = riot_response.get(friend_key)
        except AttributeError:
            return HttpResponse(INVALID_RIOT_RESPONSE)

        try:
            # use the summoner id to get the friends league information
            args = {"request": 4, "summoner_ids": friend.id}
            riot_response = riot_request(region, args)
        except APIError as e:
            if e.error_code == 404:
                return HttpResponse(SUMMONER_NOT_RANKED)
            else:
                return HttpResponse(INVALID_RIOT_RESPONSE)

        try:
            # extract the league data
            leagues = riot_response.get(str(friend.id))

            # iterate over the leagues looking for the dynamic queue league
            league = None
            for item in leagues:
                if item.queue == "RANKED_SOLO_5x5":
                    league = item

            # ensure the dynamic queue league was found
            if league is None:
                return HttpResponse(SUMMONER_NOT_RANKED)

            # iterate over the league entries to get more detailed information
            division, lp, wins, losses, series = None, None, None, None, ""
            for entry in league.entries:
                if entry.playerOrTeamId == str(friend.id):
                    division = entry.division
                    lp = entry.leaguePoints
                    wins = entry.wins
                    losses = entry.losses
                    if entry.miniSeries is not None:
                        series = entry.miniSeries.progress

            # use the gathered information to create a summoner object
            friend_o = Summoner.objects.create(
                region=region,
                key=friend_key,
                name=friend.name,
                summoner_id=friend.id,
                tier=league.tier,
                division=division,
                lp=lp,
                wins=wins,
                losses=losses,
                series=series,
                profile_icon=friend.profileIconId)
        except (AttributeError, IntegrityError):
            return HttpResponse(INVALID_RIOT_RESPONSE)

    # add the friends key to the users friend list
    if user_o.friends != "":
        user_o.friends += "," + friend_key
    else:
        user_o.friends = friend_key
    Summoner.objects.filter(pk=user_o.pk).update(friends=user_o.friends)

    # return the friends summoner object
    return HttpResponse(summoner_serializer(friend_o, None, False))


def change_email(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")
    password = data.get("password")
    new_email = data.get("new_email")

    # ensure the data is valid
    if None in (region, key, password, new_email):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    try:
        # get summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
        Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # make sure user object exists
    if summoner_o.user is None:
        return HttpResponse(SUMMONER_NOT_REGISTERED)

    # ensure password is correct
    if not hashers.check_password(password, summoner_o.user.password):
        return HttpResponse(INVALID_CREDENTIALS)

    # change email
    User.objects.filter(pk=summoner_o.user.pk).update(email=new_email)

    # return the users summoner object with the email included
    return HttpResponse(summoner_serializer(summoner_o, new_email, False))


def change_password(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")
    current_password = data.get("current_password")
    new_password = data.get("new_password")

    # ensure the data is valid
    if None in (region, key, current_password, new_password):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    try:
        # get summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
        Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # make sure user object exists
    if summoner_o.user is None:
        return HttpResponse(SUMMONER_NOT_REGISTERED)

    # make sure entered password is correct password
    if not hashers.check_password(current_password, summoner_o.user.password):
        return HttpResponse(INVALID_CREDENTIALS)

    # change password
    User.objects.filter(pk=summoner_o.user.pk).update(password=hashers.make_password(new_password))

    # return the users summoner object
    return HttpResponse(summoner_serializer(summoner_o, None, False))


def get_summoners(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # initialize empty array, to be populated with requested summoner objects
    summoners = []

    # iterate over each key
    for key in keys:
        try:
            # get summoner object
            summoner_o = Summoner.objects.get(region=region, key=format_key(key))
            Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())

            # append summoner object to array
            summoners.append(summoner_o)
        except Summoner.DoesNotExist:
            return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # remove duplicates
    summoners = set(summoners)

    # return the requested summoner objects
    return HttpResponse(summoner_serializer(summoners, None, True))


def login_user(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")
    password = data.get("password")

    # ensure the data is valid
    if None in (region, key, password):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    try:
        # get the summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
        Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # make sure user object exists
    if summoner_o.user is None:
        return HttpResponse(SUMMONER_NOT_REGISTERED)

    # make sure passwords match
    if not hashers.check_password(password, summoner_o.user.password):
        return HttpResponse(INVALID_CREDENTIALS)

    # get the users email
    email = summoner_o.user.email

    # return the users summoner object with the email included
    return HttpResponse(summoner_serializer(summoner_o, email, False))


def register_user(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")
    email = data.get("email")
    password = data.get("password")
    code = data.get("code")

    # ensure the data is valid
    if None in (region, key, email, password, code):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    # initialize summoner object value to None
    summoner_o = None

    # initialize riot response summoner value to None
    summoner = None

    try:
        # get the summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
        Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())

        # check if the user object already exists
        if summoner_o.user is not None:
            return HttpResponse(SUMMONER_ALREADY_REGISTERED)

        # get the summoner id
        summoner_id = summoner_o.summoner_id
    except Summoner.DoesNotExist:
        try:
            # summoner not in database, request summoner data from riot
            args = {"request": 1, "key": key}
            riot_response = riot_request(region, args)
        except APIError as e:
            if e.error_code == 404:
                return HttpResponse(SUMMONER_DOES_NOT_EXIST)
            else:
                return HttpResponse(INVALID_RIOT_RESPONSE)

        try:
            # extract the summoner
            summoner = riot_response.get(key)

            # get the summoner id
            summoner_id = summoner.id
        except AttributeError:
            return HttpResponse(INVALID_RIOT_RESPONSE)

    try:
        # use the summoner id to get rune page information to validate ownership
        args = {"request": 6, "summoner_id": summoner_id}
        riot_response = riot_request(region, args)

        # extract the summoners rune pages
        rune_pages = riot_response.get(str(summoner_id)).pages

        # iterate over the pages looking for one whose name matches the code
        no_match = True
        for page in rune_pages:
            if page.name == code:
                no_match = False
                break

        # return error if no match found
        if no_match:
            return HttpResponse(RUNE_PAGE_CODE_NOT_FOUND)
    except (APIError, AttributeError):
        return HttpResponse(INVALID_RIOT_RESPONSE)

    # hash password
    password = hashers.make_password(password)

    # if summoner object already exists wrap up registration
    if summoner_o is not None:
        # create a user object for the summoner object
        summoner_o.user = User.objects.create(email=email, password=password)
        Summoner.objects.filter(pk=summoner_o.pk).update(user=summoner_o.user)

        # return the users summoner object with the email included
        return HttpResponse(summoner_serializer(summoner_o, email, False))

    try:
        # summoner object did not already exist, use summoner id to get league information
        args = {"request": 4, "summoner_ids": summoner_id}
        riot_response = riot_request(region, args)
    except APIError as e:
        if e.error_code == 404:
            return HttpResponse(SUMMONER_NOT_RANKED)
        else:
            return HttpResponse(INVALID_RIOT_RESPONSE)

    try:
        # extract the league data
        leagues = riot_response.get(str(summoner_id))

        # iterate over the leagues looking for the dynamic queue league
        league = None
        for item in leagues:
            if item.queue == "RANKED_SOLO_5x5":
                league = item

        # ensure the dynamic queue league was found
        if league is None:
            return HttpResponse(SUMMONER_NOT_RANKED)

        # iterate over the league entries to get more detailed information
        division, lp, wins, losses, series = None, None, None, None, ""
        for entry in league.entries:
            if entry.playerOrTeamId == str(summoner_id):
                division = entry.division
                lp = entry.leaguePoints
                wins = entry.wins
                losses = entry.losses
                if entry.miniSeries is not None:
                    series = entry.miniSeries.progress

        # create a new user object
        user_o = User.objects.create(email=email, password=password)

        # use the gathered information to create a summoner object
        summoner_o = Summoner.objects.create(
            user=user_o,
            region=region,
            key=key,
            name=summoner.name,
            summoner_id=summoner_id,
            tier=league.tier,
            division=division,
            lp=lp,
            wins=wins,
            losses=losses,
            series=series,
            profile_icon=summoner.profileIconId)

        # return the users summoner object with the email included
        return HttpResponse(summoner_serializer(summoner_o, email, False))
    except (AttributeError, IntegrityError):
        return HttpResponse(INVALID_RIOT_RESPONSE)


def remove_friend(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    user_key = data.get("user_key")
    friend_key = data.get("friend_key")

    # ensure the data is valid
    if None in (region, user_key, friend_key):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    user_key = format_key(user_key)
    friend_key = format_key(friend_key)

    # make sure friend is not the user
    if user_key == friend_key:
        return HttpResponse(FRIEND_EQUALS_USER)

    try:
        # get the users summoner object
        user_o = Summoner.objects.get(region=region, key=user_key)
        Summoner.objects.filter(pk=user_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # remove the friends key from the users friend list
    friends = user_o.friends.split(",")
    updated_friends = []
    for friend in friends:
        if friend != friend_key:
            updated_friends.append(friend)
    user_o.friends = ",".join(updated_friends)

    # ensure proper formatting
    user_o.friends = user_o.friends.replace(",,", ",")
    if user_o.friends != "" and user_o.friends[0] == ",":
        user_o.friends = user_o.friends[1:]
    if user_o.friends != "" and user_o.friends[len(user_o.friends) - 1] == ",":
        user_o.friends = user_o.friends[:len(user_o.friends) - 1]
    Summoner.objects.filter(pk=user_o.pk).update(friends=user_o.friends)

    # return the users updated summoner object
    return HttpResponse(summoner_serializer(user_o, None, False))


def reset_password(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")
    email = data.get("email")

    # ensure the data is valid
    if None in (region, key, email):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    try:
        # get summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
        Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())
    except Summoner.DoesNotExist:
        return HttpResponse(SUMMONER_NOT_IN_DATABASE)

    # make sure the user object exists
    if summoner_o.user is None:
        return HttpResponse(SUMMONER_NOT_REGISTERED)

    # make sure the provided email matches the stored email
    if email != summoner_o.user.email:
        return HttpResponse(INVALID_CREDENTIALS)

    # generate a random password
    new_password = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10))

    # assign the generated password to the user object
    User.objects.filter(pk=summoner_o.user.pk).update(password=hashers.make_password(new_password))

    # send email to user
    email = EmailMessage("Portal: Password Reset", 'New Password: ' + new_password, to=[summoner_o.user.email])
    email.send(fail_silently=False)

    # return the users summoner object
    return HttpResponse(summoner_serializer(summoner_o, None, False))


def test1(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")

    # ensure the data is valid
    if None in (region, key):
        return HttpResponse(INVALID_REQUEST_FORMAT)

    # ensure proper key format
    key = format_key(key)

    try:
        # request summoner data from riot
        args = {"request": 1, "key": key}
        riot_response = riot_request(region, args)
    except APIError as e:
        if e.error_code == 404:
            return HttpResponse(SUMMONER_DOES_NOT_EXIST)
        else:
            return HttpResponse(INVALID_RIOT_RESPONSE)

    try:
        # extract the summoner
        summoner = riot_response.get(key)
    except AttributeError:
        return HttpResponse(INVALID_RIOT_RESPONSE)

    # return summoner object
    return HttpResponse(FRIEND_EQUALS_USER)
