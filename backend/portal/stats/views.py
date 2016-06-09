from django.shortcuts import render
from portal.errors import invalid_request_format
from portal.errors import summoner_does_not_exist
from portal.riot_api import format_key
from rest_framework.response import Response
from rest_framework.views import APIView
from summoners.models import Summoner

from .models import MatchStats
from .serializers import MatchStatsSerializer

class GetMatchStats(APIView):
    def post(self, request, format=None):
        # extract data
        data = request.data
        keys = data.get("keys")
        champion = data.get("champion")
        lane = data.get("lane")
        role = data.get("role")

        # validate required data
        if keys is None:
            return Response(invalid_request_format)

        # gather the stats for each summoner, store in list
        stats = []
        for key in keys:
            try:
                summoner = Summoner.objects.get(key = format_key(key))
            except Summoner.DoesNotExist:
                return Response(summoner_does_not_exist)
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
