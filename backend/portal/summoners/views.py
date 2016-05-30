from django.shortcuts import render
from django.core import serializers
from django.contrib.auth import hashers

from rest_framework.views import APIView
from rest_framework.response import Response

from summoners.models import User
from summoners.models import Summoner
from summoners.serializers import UserSerializer
from summoners.serializers import SummonerSerializer

class RegisterUser(APIView):
    def post(self, request, format=None):
        data = request.data
        data["user"]["password"] = hashers.make_password(data["user"]["password"])

        try:
            summoner = Summoner.objects.get(region=data["region"], name=data["name"])
            if summoner.user is None:
                summoner.user = User.objects.create(password=data["user"]["password"])
                summoner.profile_icon = data["profile_icon"]
                summoner.save()
                serializer = SummonerSerializer(summoner)
                return Response(serializer.data)
            else:
                return Response("this user is already registered")
        except Summoner.DoesNotExist:
            pass

        serializer = SummonerSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response("serializer not valid")

class LoginUser(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response('Goodbye')

class GetSummoners(APIView):
    def post(self, request, format=None):
        data = request.data
        summoners = []

        for entry in data:
            summoners.append(Summoner.objects.filter(region=entry["region"], name=entry["name"]))
        serializer = SummonerSerializer(summoners, many=True)
        return Response(serializer.data)
        return Response("serializer not valid")

class CreateSummoner(APIView):
    def post(self, request, format=None):
        region = request.data["region"]
        name = request.data["name"]
        profile_icon = request.data["profile_icon"]

        try:
            summoner = Summoner.objects.get(region=region, name=name)
            serializer = SummonerSerializer(summoner)
            return Response(serializer.data)
        except Summoner.DoesNotExist:
            pass

        summoner = Summoner.objects.create(region=region, name=name, profile_icon=profile_icon)
        serializer = SummonerSerializer(summoner)
        return Response(serializer.data)







