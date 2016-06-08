from django.contrib.auth import hashers
from django.core import serializers
from django.shortcuts import render

from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Summoner
from .models import User
from .riotapi import get_summoner
from .serializers import SummonerSerializer
from .serializers import UserSerializer

class RegisterUser(APIView):
    def post(self, request, format = None):
        # extract data
        data = request.data
        user = data.get("user")
        region = data.get("region")
        key = data.get("key")

        # validate
        if None in (user, region, key):
            return Response("error")

        key = format_key(key)

        # hash password
        password = user.get("password")
        if password is None:
            return Response("error")
        password = hashers.make_password(password)

        # check if the summoner object exists
        try:
            summoner = Summoner.objects.get(region = region, key = key)
            # check if the user object exists
            if summoner.user is None:
                # no user attached to this summoner object, create one
                summoner.user = User.objects.create(password = password)
                summoner.save()
                # return the summoner object
                return Response(SummonerSerializer(summoner).data)
            else:
                # this summoner already has a registered user, return message
                return Response("this user is already registered")
        # summoner object needs to be created
        except Summoner.DoesNotExist:
            pass

        # get more information on the summoner via riot
        summoner = get_summoner(region, key)

        # update the data before passing to serializer
        data.get("user").update({"password" : password})
        data.update({"name" : summoner.get("name"), \
                     "summoner_id" : summoner.get("id"), \
                     "profile_icon" : summoner.get("profileIconId")})

        # create summoner object with attached user and serialize it
        serializer = SummonerSerializer(data = data)
        if serializer.is_valid():
            serializer.save()
            # return the summoner object
            return Response(serializer.data)
        # validation error occured during serialization, return error
        return Response("error")

class LoginUser(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        key = data.get("key")
        password = data.get("password")

        # validate
        if None in (region, key, password):
            return Response("error")

        key = format_key(key)

        try:
            summoner = Summoner.objects.get(region = region, key = key)
            if hashers.check_password(password, summoner.user.password):
                return Response(SummonerSerializer(summoner).data)
            return Response("incorrect combination")
        except Summoner.DoesNotExist:
            return Response("incorrect combination")

class GetSummoners(APIView):
    def post(self, request, format=None):
        # set data and initialize array for storing summoner objects
        data = request.data
        summoners = []

        # get all requested summoner objects
        for entry in data:
            region = entry.get("region")
            key = format_key(entry.get("key"))
            summoners.append(Summoner.objects.get(region = region, key = key))
        
        # return the summoner objects
        return Response(SummonerSerializer(summoners, many=True).data)

class AddFriend(APIView):
    def post(Self, request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        summoner_key = data.get("summoner_key")
        friend_key = data.get("friend_key")

        # validate
        if None in (region, summoner_key, friend_key):
            return Response("error")

        summoner_key = format_key(summoner_key)
        friend_key = format_key(friend_key)

        # get the users summoner object
        summoner = Summoner.objects.get(region = region, key = summoner_key) 

        # ensure a summoner object exists for the friend
        ensure_summoner_exists(region, friend_key)

        # add the friends key to the users friend list
        if summoner.friends is not None:
            summoner.friends += friend_key + ","
        else:
            summoner.friends = friend_key + ","
        summoner.save()

        # return the users updated summoner object
        return Response(SummonerSerializer(summoner).data)

# used to ensure a summoner exists in the database
def ensure_summoner_exists(region, key):
    try:
        return Summoner.objects.get(region = region, key = key)
    except Summoner.DoesNotExist:
        pass

    # get the summoners information via riot
    summoner = get_summoner(region, key)

    # use gathered info to create summoner in database
    summoner = Summoner.objects.create(region = region, \
                                       key = key, \
                                       name = summoner.get("name"), \
                                       profile_icon = summoner.get("profile_icon"))

    # returns a summoner model object
    return summoner

def format_key(key):
    # remove white spaces and make all lowercase
    return(key.replace(" ", "").lower())
    












