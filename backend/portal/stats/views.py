from django.http import JsonResponse
from portal.errors import INVALID_REQUEST_FORMAT
from portal.riot import format_key
from stats.models import MatchStats
from stats.models import SeasonStats
from stats.serializers import stats_serializer


def get_match_stats(request):
    # extract data
    data = request.data
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return JsonResponse(INVALID_REQUEST_FORMAT)

    # initialize list for storing the requested stats
    stats = []

    # iterate over the list of keys
    for key in keys:
        # ensure proper key format
        key = format_key(key)

        # get the stats from the database
        query = MatchStats.objects.filter(region=region, summoner_key=key).order_by("-match_creation")[:20]
        stats.extend(query)

    # return the stats
    return JsonResponse(stats_serializer(stats))


def get_season_stats(request):
    # extract data
    data = request.data
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return JsonResponse(INVALID_REQUEST_FORMAT)

    # initialize list for storing the requested stats
    stats = []

    # iterate over the list of keys
    for key in keys:
        # ensure proper key format
        key = format_key(key)

        # get the stats from the database
        query = SeasonStats.objects.filter(region=region, summoner_key=key)
        stats.extend(query)

    # return the stats
    return JsonResponse(stats_serializer(stats))
