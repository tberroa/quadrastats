from django.contrib.auth import hashers
from django.core import serializers
from django.core.mail import send_mail
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
from portal.riot_api import format_key
from portal.riot_api import get_summoner
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Summoner
from .models import User
from .serializers import SummonerSerializer
from .serializers import UserSerializer

class AddFriend(APIView):
    def post(Self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        user_key = data.get("user_key")
        friend_key = data.get("friend_key")

        # validate
        if None in (region, user_key, friend_key):
            return Response(invalid_request_format)

        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(friend_equals_user)

        # get the users summoner object
        try:
            user = Summoner.objects.get(region = region, key = user_key)
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
            friend_o = Summoner.objects.get(region = region, key = friend_key)
        except Summoner.DoesNotExist:
            val = get_summoner(region, friend_key)
            if val[0] != 200:
                return Response(invalid_riot_response)
            else:
                friend = val[1]
                friend_o = Summoner.objects.create(region = region, \
                                                   key = friend_key, \
                                                   name = friend.get("name"), \
                                                   summoner_id = friend.get("id"), \
                                                   profile_icon = friend.get("profileIconId"))

        # add the friends key to the users friend list
        if user.friends != "":
            user.friends += "," + friend_key
        else:
            user.friends = friend_key
        user.save()

        # return the friends summoner object
        return Response(SummonerSerializer(friend_o).data)

class ChangePassword(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        old_password = data.get("old_password")
        new_password = data.get("new_password")

        # validate
        if None in (region, key, old_password, new_password):
           return Response(invalid_request_format)

        # make sure summoner object exists
        try:
            summoner = Summoner.objects.get(region = region, key = key)
            # make sure user object exists
            if summoner.user is not None:
                # make sure old password is correct password
                if hashers.check_password(old_password, summoner.user.password):
                    # change password
                    summoner.user.password = hashers.make_password(new_password)
                    summoner.user.save()
                    summoner.save()
                    return Response(SummonerSerializer(summoner).data)
                return Response(invalid_credentials)
            else:
                return Response(invalid_credentials)
        except Summoner.DoesNotExist:
            return Response(invalid_credentials)

class ForgotPassword(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")    
        email = data.get("email")

        # validate
        if None in (region, key, email):
            return Response(invalid_request_format)

        # send email
        send_mail(
            'Portal: Requested Password',
            'Here is the message.',
            'tberroa@outlook.com',
            [email],
            fail_silently=False,
        )

        return Response(invalid_credentials)

class GetSummoners(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        keys = data.get("keys")

        # validate
        if None in (region, keys):
            return Response(invalid_request_format)

        # get all requested summoner objects
        summoners = []
        for key in keys:
            try:
                summoners.append(Summoner.objects.get(region = region, key = format_key(key)))
            except Summoner.DoesNotExist:
                return Response(summoner_does_not_exist)

        # remove duplicates
        summoners = set(summoners)

        # return the summoner objects
        return Response(SummonerSerializer(summoners, many=True).data)

class LoginUser(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        password = data.get("password")

        # validate
        if None in (region, key, password):
            return Response(invalid_request_format)

        key = format_key(key)

        # make sure summoner object exists
        try:
            summoner = Summoner.objects.get(region = region, key = key)
            # make sure user object exists
            if summoner.user is not None:
                # make sure passwords match
                if hashers.check_password(password, summoner.user.password):
                    return Response(SummonerSerializer(summoner).data)
                return Response(invalid_credentials)
            else:
                return Response(invalid_credentials)
        except Summoner.DoesNotExist:
            return Response(invalid_credentials)

class RegisterUser(APIView):
    def post(self, request, format = None):
        # extract data
        data = request.data
        user = data.get("user")
        region = data.get("region")
        key = data.get("key")

        # validate
        if None in (user, region, key):
            return Response(invalid_request_format)

        key = format_key(key)

        # make sure email was entered
        email = user.get("email")
        if email is None:
            return Response(invalid_request_format)

        # hash password
        password = user.get("password")
        if password is None:
            return Response(invalid_request_format)
        password = hashers.make_password(password)

        # check if the summoner object exists
        try:
            summoner = Summoner.objects.get(region = region, key = key)
            # check if the user object exists
            if summoner.user is None:
                summoner.user = User.objects.create(email = email, password = password)
                summoner.save()
                return Response(SummonerSerializer(summoner).data)
            else:
                return Response(summoner_already_registered)
        except Summoner.DoesNotExist:
            pass

        # get more information on the summoner via riot
        val = get_summoner(region, key)
        if val[0] != 200:
            return Response(invalid_riot_response)
        else:
            summoner = val[1]

        # update the data before passing to serializer
        data.get("user").update({"password" : password})
        data.update({"key" : key, \
                     "name" : summoner.get("name"), \
                     "summoner_id" : summoner.get("id"), \
                     "profile_icon" : summoner.get("profileIconId")})

        # create summoner object with attached user and serialize it
        serializer = SummonerSerializer(data = data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(internal_processing_error)

class RemoveFriend(APIView):
    def post(Self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        user_key = data.get("user_key")
        friend_key = data.get("friend_key")

        # validate
        if None in (region, user_key, friend_key):
            return Response(invalid_request_format)

        user_key = format_key(user_key)
        friend_key = format_key(friend_key)

        # make sure friend is not the user
        if user_key == friend_key:
            return Response(friend_equals_user)

        # get the users summoner object
        try:
            user = Summoner.objects.get(region = region, key = user_key)
        except Summoner.DoesNotExist:
            return Response(summoner_does_not_exist)

        # remove the friends key from the users friend list
        user.friends = user.friends.replace(friend_key, "")

        # ensure proper formatting
        user.friends = user.friends.replace(",,", ",")
        if user.friends != "" and user.friends[0] == ",":
            user.friends = user.friends[1:]
        if user.friends != "" and user.friends[len(user.friends)-1] == ",":
            user.friends = user.friends[:len(user.friends)-1]
        user.save()

        # return the users updated summoner object
        return Response(SummonerSerializer(user).data)
    



