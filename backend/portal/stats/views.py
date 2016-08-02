from django.shortcuts import render
from portal.errors import invalid_request_format
from portal.errors import summoner_does_not_exist
from portal.tasks import format_key
from rest_framework.response import Response
from rest_framework.views import APIView
from stats.models import MatchStats
from stats.models import SeasonStats
from stats.serializers import MatchStatsSerializer
from stats.serializers import SeasonStatsSerializer
from summoners.models import Summoner


class GetMatchStats(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        keys = data.get("keys")
        champion = data.get("champion")
        lane = data.get("lane")
        role = data.get("role")

        # validate required data
        if None in (region, keys):
            return Response(invalid_request_format)

        # initialize list for storing the requested match stats
        stats = []

        # iterate over the list of keys
        for key in keys:
            # get summoner object
            try:
                summoner = Summoner.objects.get(region=region, key=format_key(key))
            except Summoner.DoesNotExist:
                return Response(summoner_does_not_exist)

            # construct the query based on given parameters
            query = MatchStats.objects.filter(summoner_id=summoner.summoner_id).order_by("-match_creation")
            if champion is not None and champion != 0:
                query = query.filter(champion=champion)
            if lane is not None:
                query = query.filter(lane=lane)
            if role is not None:
                query = query.filter(role=role)

            # execute query, only store the 20 most recent entries
            stats += query[:20]

        # return the list of match stats
        return Response(MatchStatsSerializer(stats, many=True).data)


class GetSeasonStats(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        data = request.data
        return Response(data)
