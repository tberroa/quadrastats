import random
import string
from cassiopeia.type.api.exception import APIError
from datetime import datetime
from django.contrib.auth import hashers
from django.core import serializers
from django.core.mail import EmailMessage
from django.db.utils import IntegrityError
from django.shortcuts import render
from portal.errors import FRIEND_ALREADY_LISTED
from portal.errors import FRIEND_EQUALS_USER
from portal.errors import FRIEND_LIMIT_REACHED
from portal.errors import INVALID_CREDENTIALS
from portal.errors import INVALID_REQUEST_FORMAT
from portal.errors import INVALID_RIOT_RESPONSE
from portal.errors import RUNE_PAGE_CODE_NOT_FOUND
from portal.errors import SUMMONER_ALREADY_REGISTERED
from portal.errors import SUMMONER_DOES_NOT_EXIST
from portal.errors import SUMMONER_NOT_RANKED
from portal.errors import SUMMONER_NOT_REGISTERED
from portal.tasks import format_key
from portal.tasks import riot_request
from rest_framework.response import Response
from rest_framework.views import APIView
from summoners.models import Summoner
from summoners.models import User
from summoners.serializers import SummonerSerializer
from summoners.serializers import UserSerializer


class AddFriend(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        user_key = data.get("user_key")
        friend_key = data.get("friend_key")

        # validate
        if None in (region, user_key, friend_key):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(FRIEND_EQUALS_USER)

        try:
            # get the users summoner object
            user = Summoner.objects.get(region=region, key=user_key)
            Summoner.objects.filter(pk=user.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # check if user is at friend limit or if friend is already listed 
        if user.friends is not None:
            friends = user.friends.split(",")
            if len(friends) >= 20:
                return Response(FRIEND_LIMIT_REACHED)
            for friend in friends:
                if friend == friend_key:
                    return Response(FRIEND_ALREADY_LISTED)

        try:
            # check if a summoner object exists for the friend
            friend_o = Summoner.objects.get(region=region, key=friend_key)
            Summoner.objects.filter(pk=friend_o.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            try:
                # summoner not in database, request summoner data from Riot
                args = {"request": 1, "key": friend_key}
                riot_response = riot_request(region, args)

                # extract the summoner
                friend = riot_response.get(friend_key)

                # use the summoner id to get the friends league information
                args = {"request": 4, "summoner_ids": friend.id}
                riot_response = riot_request(region, args)

                # extract the league data
                leagues = riot_response.get(str(friend.id))

                # iterate over the leagues looking for the dynamic queue league
                league = None
                for item in leagues:
                    if item.queue == "RANKED_SOLO_5x5":
                        league = item

                # ensure the dynamic queue league was found
                if league is None:
                    return Response(SUMMONER_NOT_RANKED)

                # iterate over the league entries to get more detailed information
                division = None
                lp = None
                wins = None
                losses = None
                series = ""
                for entry in league.entries:
                    if entry.playerOrTeamId == str(friend.id):
                        division = entry.division
                        lp = entry.leaguePoints
                        wins = entry.wins
                        losses = entry.losses
                        if entry.miniSeries is not None:
                            series = mini_series.get("progress")

                # use the gathered information to create a summoner object
                friend_o = Summoner.objects.create(region=region,
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
            except (APIError, AttributeError, IntegrityError):
                return Response(INVALID_RIOT_RESPONSE)

        # add the friends key to the users friend list
        if user.friends != "":
            user.friends += "," + friend_key
        else:
            user.friends = friend_key
        Summoner.objects.filter(pk=user.pk).update(friends=user.friends)

        # return the friends summoner object
        return Response(SummonerSerializer(friend_o).data)


class ChangeEmail(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        password = data.get("password")
        new_email = data.get("new_email")

        # validate
        if None in (region, key, password, new_email):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        key = format_key(key)

        try:
            # get summoner object
            summoner = Summoner.objects.get(region=region, key=key)
            Summoner.objects.filter(pk=summoner.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # make sure user object exists
        if summoner.user is None:
            return Response(SUMMONER_NOT_REGISTERED)

        # ensure password is correct
        if not hashers.check_password(password, summoner.user.password):
            return Response(INVALID_CREDENTIALS)

        # change email
        User.objects.filter(pk=summoner.user.pk).update(email=new_email)
        Summoner.objects.filter(pk=summoner.pk).update(user=User.objects.get(pk=summoner.user.pk))

        # serialize the summoner object
        return_json = SummonerSerializer(summoner).data

        # include the email
        return_json.update({"email": new_email})

        # return the users summoner object with the email included
        return Response(return_json)


class ChangePassword(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        current_password = data.get("current_password")
        new_password = data.get("new_password")

        # validate
        if None in (region, key, current_password, new_password):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        key = format_key(key)

        try:
            # get summoner object
            summoner = Summoner.objects.get(region=region, key=key)
            Summoner.objects.filter(pk=summoner.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # make sure user object exists
        if summoner.user is None:
            return Response(SUMMONER_NOT_REGISTERED)

        # make sure entered password is correct password
        if not hashers.check_password(current_password, summoner.user.password):
            return Response(INVALID_CREDENTIALS)

        # change password
        User.objects.filter(pk=summoner.user.pk).update(password=hashers.make_password(new_password))
        Summoner.objects.filter(pk=summoner.pk).update(user=User.objects.get(pk=summoner.user.pk))

        # return the users summoner object
        return Response(SummonerSerializer(summoner).data)


class GetSummoners(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        keys = data.get("keys")

        # validate
        if None in (region, keys):
            return Response(INVALID_REQUEST_FORMAT)

        # initialize empty array, to be populated with requested summoner objects
        summoners = []

        # iterate over each key
        for key in keys:
            # ensure proper key format
            key = format_key(key)

            try:
                # get summoner object
                summoner = Summoner.objects.get(region=region, key=key)
                Summoner.objects.filter(pk=summoner.pk).update(accessed=datetime.now())

                # append summoner object to array
                summoners.append(summoner)
            except Summoner.DoesNotExist:
                return Response(SUMMONER_DOES_NOT_EXIST)

        # remove duplicates
        summoners = set(summoners)

        # return the requested summoner objects
        return Response(SummonerSerializer(summoners, many=True).data)


class LoginUser(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        password = data.get("password")

        # validate
        if None in (region, key, password):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        key = format_key(key)

        try:
            # get the summoner object
            summoner = Summoner.objects.get(region=region, key=key)
            Summoner.objects.filter(pk=summoner.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # make sure user object exists
        if summoner.user is None:
            return Response(SUMMONER_NOT_REGISTERED)

        # make sure passwords match
        if not hashers.check_password(password, summoner.user.password):
            return Response(INVALID_CREDENTIALS)

        # get the users email
        email = summoner.user.email

        # serialize the summoner object
        return_json = SummonerSerializer(summoner).data

        # include the email
        return_json.update({"email": email})

        # return the users summoner object with the email included
        return Response(return_json)


class RegisterUser(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        email = data.get("email")
        password = data.get("password")
        code = data.get("code")

        # validate
        if None in (region, key, email, password, code):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        key = format_key(key)

        # hash password
        password = hashers.make_password(password)

        # initialize summoner object value to None
        summoner_o = None

        # initialize summoner name and profile icon to None
        name = None
        profile_icon = None

        try:
            # get the summoner object
            summoner_o = Summoner.objects.get(region=region, key=key)
            Summoner.objects.filter(pk=summoner_o.pk).update(accessed=datetime.now())

            # get the summoner id
            summoner_id = summoner_o.summoner_id

            # check if the user object already exists
            if summoner_o.user is not None:
                return Response(SUMMONER_ALREADY_REGISTERED)
        except Summoner.DoesNotExist:
            # get more information on the summoner via riot
            args = {"request": 1, "key": key}
            riot_response = riot_request(region, args)

            # extract the summoner
            summoner = riot_response

            # ensure data is valid
            if summoner is None:
                return Response(INVALID_RIOT_RESPONSE)

            # extract summoner field values
            name = summoner.get("name")
            summoner_id = summoner.get("id")
            profile_icon = summoner.get("profileIconId")

            # ensure the data is valid
            if None in (name, summoner_id, profile_icon):
                return Response(INVALID_RIOT_RESPONSE)

        # use the summoner id to get rune page information to validate ownership
        args = {"request": 6, "summoner_id": summoner_id}
        riot_response = riot_request(region, args)

        # make sure the response is valid
        if riot_response[0] != 200:
            return Response(INVALID_RIOT_RESPONSE)

        # extract the summoners rune page data
        rune_data = riot_response[1].get(str(summoner_id))

        # ensure data is valid
        if rune_data is None:
            return Response(INVALID_RIOT_RESPONSE)

        # extract the set of rune pages
        rune_pages = rune_data.get("pages")

        # ensure data is valid
        if rune_pages is None:
            return Response(INVALID_RIOT_RESPONSE)

        # iterate over the pages looking for one whose name matches the code
        no_match = True
        for page in rune_pages:
            # get the name
            page_name = page.get("name")

            # ensure data is valid
            if page_name is None:
                return Response(INVALID_RIOT_RESPONSE)

            # check if name matches code
            if page_name == code:
                no_match = False
                break

        # return error if no match found
        if no_match:
            return Response(RUNE_PAGE_CODE_NOT_FOUND)

        # if summoner object already exists simply create user object and attach it to summoner object
        if summoner_o is not None:
            # create a user object for the summoner object
            summoner_o.user = User.objects.create(email=email, password=password)
            Summoner.objects.filter(pk=summoner_o.pk).update(user=User.objects.get(pk=summoner_o.user.pk))

            # serialize the summoner object
            return_json = SummonerSerializer(summoner_o).data

            # include the email
            return_json.update({"email": email})

            # return the users summoner object with the email included
            return Response(return_json)

        # use the summoner id to get the summoners league information
        args = {"request": 4, "summoner_ids": str(summoner_id)}
        riot_response = riot_request(region, args)

        # make sure the response is valid
        if riot_response[0] != 200:
            return Response(INVALID_RIOT_RESPONSE)

        # extract the league data
        leagues = riot_response[1].get(str(summoner_id))

        # ensure the data is valid
        if leagues is None:
            return Response(INVALID_RIOT_RESPONSE)

        # iterate over the leagues looking for the dynamic queue league
        league = None
        for item in leagues:
            # ensure data is valid
            if item.get("queue") is None:
                return Response(INVALID_RIOT_RESPONSE)

            if item.get("queue") == "RANKED_SOLO_5x5":
                league = item

        # ensure the dynamic queue league was found
        if league is None:
            return Response(INVALID_RIOT_RESPONSE)

        # use the league data to get the rank tier
        tier = league.get("tier")

        # ensure data is valid
        if tier is None:
            return Response(INVALID_RIOT_RESPONSE)

        # extract the player entries
        entries = league.get("entries")

        # ensure data is valid
        if entries is None:
            return Response(INVALID_RIOT_RESPONSE)

        # iterate over the league entries to get more detailed information
        division = None
        lp = None
        wins = None
        losses = None
        series = ""
        for entry in entries:
            # ensure data is valid
            if entry.get("playerOrTeamId") is None:
                return Response(INVALID_RIOT_RESPONSE)

            # check player id against the summoners id
            if entry.get("playerOrTeamId") == str(summoner_id):
                # get division, lp, wins, and losses
                division = entry.get("division")
                lp = entry.get("leaguePoints")
                wins = entry.get("wins")
                losses = entry.get("losses")

                # ensure data is valid
                if None in (division, lp, wins, losses):
                    return Response(INVALID_RIOT_RESPONSE)

                # check if summoner is in series
                mini_series = entry.get("miniSeries")

                # if summoner is not in series this is None
                if mini_series is not None:
                    series = mini_series.get("progress")

        # division, lp, wins, and losses cannot be None
        if None in (division, lp, wins, losses):
            return Response(INVALID_RIOT_RESPONSE)

        # create a user data dictionary
        user_data = {"email": email, "password": password}

        # create a summoner data dictionary
        summoner_data = {"region": region,
                         "key": key,
                         "name": name,
                         "summoner_id": summoner_id,
                         "tier": tier,
                         "division": division,
                         "lp": lp,
                         "wins": wins,
                         "losses": losses,
                         "series": series,
                         "profile_icon": profile_icon}

        # create a new user object
        user_o = User.objects.create(**user_data)

        # create a new summoner object
        summoner_o = Summoner.objects.create(user=user_o, **summoner_data)

        # serialize the summoner object
        return_json = SummonerSerializer(summoner_o).data

        # include the email
        return_json.update({"email": email})

        # return the users summoner object with the email included
        return Response(return_json)


class RemoveFriend(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        user_key = data.get("user_key")
        friend_key = data.get("friend_key")

        # validate
        if None in (region, user_key, friend_key):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(FRIEND_EQUALS_USER)

        try:
            # get the users summoner object
            user = Summoner.objects.get(region=region, key=user_key)
            Summoner.objects.filter(pk=user.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # remove the friends key from the users friend list
        friends = user.friends.split(",")
        updated_friends = []
        for friend in friends:
            if friend != friend_key:
                updated_friends.append(friend)
        user.friends = ",".join(updated_friends)

        # ensure proper formatting
        user.friends = user.friends.replace(",,", ",")
        if user.friends != "" and user.friends[0] == ",":
            user.friends = user.friends[1:]
        if user.friends != "" and user.friends[len(user.friends) - 1] == ",":
            user.friends = user.friends[:len(user.friends) - 1]
        Summoner.objects.filter(pk=user.pk).update(friends=user.friends)

        # return the users updated summoner object
        return Response(SummonerSerializer(user).data)


class ResetPassword(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        email = data.get("email")

        # validate
        if None in (region, key, email):
            return Response(INVALID_REQUEST_FORMAT)

        # ensure proper key format
        key = format_key(key)

        try:
            # get summoner object
            summoner = Summoner.objects.get(region=region, key=key)
            Summoner.objects.filter(pk=summoner.pk).update(accessed=datetime.now())
        except Summoner.DoesNotExist:
            return Response(SUMMONER_DOES_NOT_EXIST)

        # make sure the user object exists
        if summoner.user is None:
            return Response(SUMMONER_NOT_REGISTERED)

        # make sure the provided email matches the stored email
        if email != summoner.user.email:
            return Response(INVALID_CREDENTIALS)

        # generate a random password
        new_password = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10))

        # assign the generated password to the user object
        User.objects.filter(pk=summoner.user.pk).update(password=hashers.make_password(new_password))
        Summoner.objects.filter(pk=summoner.pk).update(user=User.objects.get(pk=summoner.user.pk))

        # send email to user
        email = EmailMessage("Portal: Password Reset", 'New Password: ' + new_password, to=[summoner.user.email])
        email.send(fail_silently=False)

        # return the users summoner object
        return Response(SummonerSerializer(summoner).data)
