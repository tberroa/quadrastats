from django.contrib.auth import hashers
from django.core import serializers
from django.shortcuts import render

from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Summoner
from .models import User
from .serializers import SummonerSerializer
from .serializers import UserSerializer
from .utils import create_summoner

class RegisterUser(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        password = hashers.make_password(data["user"]["password"])
        region = data["region"]
        name = data["name"]
        profile_icon = data["profile_icon"]

        # check if the summoner object exists
        try:
            summoner = Summoner.objects.get(region=region, name=name)
            # check if the user object exists
            if summoner.user is None:
                # no user attached to this summoner object, create one
                summoner.user = User.objects.create(password=password)
                summoner.profile_icon = profile_icon
                summoner.save()
                serializer = SummonerSerializer(summoner)
                # return the summoner object
                return Response(serializer.data)
            else:
                # this summoner already has a registered user, return message
                return Response("this user is already registered")
        except Summoner.DoesNotExist:
            # this summoner does not exist yet, needs to be created
            pass

        # create summoner object with attached user and serialize it in one step
        serializer = SummonerSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            # return the summoner object
            return Response(serializer.data)
        # validation error occured during serialization, return error
        return Response(serializer.errors)

class LoginUser(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        region = data["region"]
        name = data["name"]
        password = data["password"]

        try:
            summoner = Summoner.objects.get(region=region, name=name)
            if hashers.check_password(password, summoner.user.password):
                return Response(SummonerSerializer(summoner).data)
            return Response('incorrect combination')
        except Summoner.DoesNotExist:
            return Response('incorrect combination')

class CreateSummoner(APIView):
    def post(self, request, format=None):
        # extract data
        region = request.data["region"]
        name = request.data["name"]
        profile_icon = request.data["profile_icon"]

        # create summoner object
        summoner = create_summoner(region, name, profile_icon)

        # return summoner object
        return Response(SummonerSerializer(summoner).data)

class GetSummoners(APIView):
    def post(self, request, format=None):
        # set data and initialize array for storing summoner objects
        data = request.data
        summoners = []

        # get all requested summoner objects
        for entry in data:
            summoners.append(Summoner.objects.get(region=entry["region"], name=entry["name"]))
        
        # return the summoner objects
        return Response(SummonerSerializer(summoners, many=True).data)

class AddFriend(APIView):
    def post(Self, request, format=None):
        # extract data
        data = request.data
        region = data["region"]
        summoner_name = data["summoner_name"]
        friend_name = data["friend_name"]
        friend_profile_icon = data["friend_profile_icon"]

        # get the users summoner object
        summoner = Summoner.objects.get(region=region, name=summoner_name) 
        # ensure a summoner object exists for the friend
        create_summoner(region, friend_name, friend_profile_icon)

        # add the friends name to the users friend list
        if summoner.friends is None:
            summoner.friends = friend_name + ","
        else:
            summoner.friends += friend_name + ","
        summoner.save()

        # return the users updated summoner object
        return Response(SummonerSerializer(summoner).data)





