import random
import string
from django.contrib.auth import hashers
from django.core import serializers
from django.core.mail import EmailMessage
from django.shortcuts import render
from portal.errors import friend_already_listed
from portal.errors import friend_equals_user
from portal.errors import friend_limit_reached
from portal.errors import internal_processing_error
from portal.errors import invalid_credentials
from portal.errors import invalid_request_format
from portal.errors import invalid_riot_response
from portal.errors import summoner_already_registered
from portal.errors import summoner_does_not_exist
from portal.errors import summoner_not_registered
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
            return Response(invalid_request_format)

        # ensure proper key format
        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(friend_equals_user)

        # get the users summoner object
        try:
            user = Summoner.objects.get(region=region, key=user_key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # check if user is at friend limit or if friend is already listed 
        if user.friends is not None:
            friends = user.friends.split(",")
            if len(friends) >= 20:
                return Response(friend_limit_reached)
            for friend in friends:
                if friend == friend_key:
                    return Response(friend_already_listed)

        # ensure a summoner object exists for the friend
        try:
            friend_o = Summoner.objects.get(region=region, key=friend_key)
        except Summoner.DoesNotExist:
            args = {"request": 1, "key": friend_key}
            val = riot_request(region, args)
            if val[0] != 200:
                return Response(invalid_riot_response)
            else:
                friend = val[1]
                friend_o = Summoner.objects.create(region=region,
                                                   key=friend_key,
                                                   name=friend.get("name"),
                                                   summoner_id=friend.get("id"),
                                                   profile_icon=friend.get("profileIconId"))

        # add the friends key to the users friend list
        if user.friends != "":
            user.friends += "," + friend_key
        else:
            user.friends = friend_key
        user.save()

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
            return Response(invalid_request_format)

        # ensure proper key format
        key = format_key(key)

        # get summoner object
        try:
            summoner = Summoner.objects.get(region=region, key=key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # make sure user object exists
        if summoner.user is None:
            return Response(summoner_not_registered)

        # ensure password is correct
        if not hashers.check_password(password, summoner.user.password):
            return Response(invalid_credentials)

        # change email
        summoner.user.email = new_email
        summoner.user.save()
        summoner.save()

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
            return Response(invalid_request_format)

        # ensure proper key format
        key = format_key(key)

        # get summoner object
        try:
            summoner = Summoner.objects.get(region=region, key=key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # make sure user object exists
        if summoner.user is None:
            return Response(summoner_not_registered)

        # make sure entered password is correct password
        if not hashers.check_password(current_password, summoner.user.password):
            return Response(invalid_credentials)

        # change password
        summoner.user.password = hashers.make_password(new_password)
        summoner.user.save()
        summoner.save()

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
            return Response(invalid_request_format)

        # initialize empty array, to be populated with requested summoner objects
        summoners = []

        # iterate over each key
        for key in keys:
            # ensure proper key format
            key = format_key(key)

            # get summoner object and append to array
            try:
                summoners.append(Summoner.objects.get(region=region, key=key))
            except Summoner.DoesNotExist:
                return Response(summoner_does_not_exist)

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
            return Response(invalid_request_format)

        # ensure proper key format
        key = format_key(key)

        # get the summoner object
        try:
            summoner = Summoner.objects.get(region=region, key=key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # make sure user object exists
        if summoner.user is None:
            return Response(summoner_not_registered)

        # make sure passwords match
        if not hashers.check_password(password, summoner.user.password):
            return Response(invalid_credentials)

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
            return Response(invalid_request_format)

        # ensure proper key format
        key = format_key(key)

        # hash password
        password = hashers.make_password(password)

        # check if the summoner object already exists
        try:
            summoner = Summoner.objects.get(region=region, key=key)

            # check if the user object already exists
            if summoner.user is not None:
                return Response(summoner_already_registered)

            # create a user object for the summoner object
            summoner.user = User.objects.create(email=email, password=password)
            summoner.save()

            # serialize the summoner object
            return_json = SummonerSerializer(summoner).data

            # include the email
            return_json.update({"email": email})

            # return the users summoner object with the email included
            return Response(return_json)

        # summoner object did not already exist, need to create it
        except Summoner.DoesNotExist:
            pass

        # get more information on the summoner via riot
        args = {"request": 1, "key": key}
        val = riot_request(region, args)
        if val[0] != 200:
            return Response(invalid_riot_response)
        else:
            summoner = val[1]

        # None check important fields
        name = summoner.get("name")
        summoner_id = summoner.get("id")
        profile_icon = summoner.get("profileIconId")
        if None in (name, summoner_id, profile_icon):
            return Response(invalid_riot_response)

        # create a user data dictionary
        user_data = {"email": email, "password": password}

        # create a summoner data dictionary
        summoner_data = {"region": region, "key": key, "name": name, "summoner_id": summoner_id,
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
            return Response(invalid_request_format)

        # ensure proper key format
        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(friend_equals_user)

        # get the users summoner object
        try:
            user = Summoner.objects.get(region=region, key=user_key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # remove the friends key from the users friend list
        user.friends = user.friends.replace(friend_key, "")

        # ensure proper formatting
        user.friends = user.friends.replace(",,", ",")
        if user.friends != "" and user.friends[0] == ",":
            user.friends = user.friends[1:]
        if user.friends != "" and user.friends[len(user.friends) - 1] == ",":
            user.friends = user.friends[:len(user.friends) - 1]
        user.save()

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
            return Response(invalid_request_format)

        # ensure proper key format
        key = format_key(key)

        # get summoner object
        try:
            summoner = Summoner.objects.get(region=region, key=key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # make sure the user object exists
        if summoner.user is None:
            return Response(summoner_not_registered)

        # make sure the provided email matches the stored email
        if email != summoner.user.email:
            return Response(invalid_credentials)

        # generate a random password
        new_password = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10))

        # assign the generated password to the user object
        summoner.user.password = hashers.make_password(new_password)
        summoner.user.save()
        summoner.save()

        # send email to user
        email = EmailMessage("Portal: Password Reset", 'New Password: ' + new_password, to=[summoner.user.email])
        email.send(fail_silently=False)

        # return the users summoner object
        return Response(SummonerSerializer(summoner).data)
