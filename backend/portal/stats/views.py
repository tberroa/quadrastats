from django.core.cache import cache
from portal.errors import INVALID_REQUEST_FORMAT
from portal.tasks import format_key
from rest_framework.response import Response
from rest_framework.views import APIView
from stats.models import MatchStats
from stats.models import SeasonStats
from stats.serializers import MatchStatsSerializer
from stats.serializers import SeasonStatsSerializer


class GetMatchStats(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        keys = data.get("keys")

        # ensure the data is valid
        if None in (region, keys):
            return Response(INVALID_REQUEST_FORMAT)

        # initialize list for storing the requested stats
        stats = []

        # iterate over the list of keys
        for key in keys:
            # ensure proper key format
            key = format_key(key)

            try:
                # get the stats from the cache
                stats.extend(cache.get(region+key+"match"))
            except TypeError:
                # get the stats from the database
                query = MatchStats.objects.filter(region=region, summoner_key=key).order_by("-match_creation")[:20]
                stats.extend(query)

                # store stats in the cache
                cache.set(region+key+"match", query, None)

        # return the stats
        return Response(MatchStatsSerializer(stats, many=True).data)


class GetSeasonStats(APIView):
    # noinspection PyUnusedLocal
    @staticmethod
    def post(request, format=None):
        # extract data
        data = request.data
        region = data.get("region")
        keys = data.get("keys")

        # ensure the data is valid
        if None in (region, keys):
            return Response(INVALID_REQUEST_FORMAT)

        # initialize list for storing the requested stats
        stats = []

        # iterate over the list of keys
        for key in keys:
            # ensure proper key format
            key = format_key(key)

            try:
                # get the stats from the cache
                stats.extend(cache.get(region+key+"season"))
            except TypeError:
                # get the stats from the database
                query = SeasonStats.objects.filter(region=region, summoner_key=key)
                stats.extend(query)

                # store stats in the cache
                cache.set(region+key+"season", query, None)

        # return the stats
        return Response(SeasonStatsSerializer(stats, many=True).data)
