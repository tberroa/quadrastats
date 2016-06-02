from django.shortcuts import render

from rest_framework.response import Response
from rest_framework.views import APIView

from summoners.models import Summoner

from .models import MatchStats
from .serializers import MatchStatsSerializer

class GetMatchStats(APIView):
    def post(self, request, format=None):
        data = request.data
        names = data["summoners"]
        champion = data["champion"]
        lane = data["lane"]
        role = data["role"]

        stats = []
        for name in names:
            summoner = Summoner.objects.get(name = name)
            query = MatchStats.objects.filter(summoner = summoner)
            if champion is not None:
                query = query.filter(champion = champion)
            if lane is not None:
                query = query.filter(lane = lane)
            if role is not None:
                query = query.filter(role = role)
            stats += query

        return Response(MatchStatsSerializer(stats, many=True).data)

class GetSeasonStats(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response(data)

class GetChampionStats(APIView):
    def post(self, request, format=None):
        data = request.data
        return Response(data)
