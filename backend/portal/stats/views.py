import json
from django.db.models import Q
from django.http import HttpResponse
from portal.errors import INVALID_REQUEST_FORMAT
from portal.riot import format_key
from stats.models import MatchStats
from stats.models import SeasonStats
from stats.serializers import stats_serializer


def get_match_stats(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # turn list of keys into list of Q objects
    queries = [Q(region=region, summoner_key=format_key(key)) for key in keys]

    # take one Q object from the list
    query = queries.pop()

    # or the Q object with the ones remaining in the list
    for item in queries:
        query |= item

    # query the database
    stats = list(MatchStats.objects.filter(query).order_by("-match_creation"))

    # return the stats
    return HttpResponse(stats_serializer(stats))


def get_season_stats(request):
    # make sure its a post
    if request.method == "POST":
        pass
    else:
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # turn list of keys into list of Q objects
    queries = [Q(region=region, summoner_key=format_key(key)) for key in keys]

    # take one Q object from the list
    query = queries.pop()

    # or the Q object with the ones remaining in the list
    for item in queries:
        query |= item

    # query the database
    stats = list(SeasonStats.objects.filter(query))

    # return the stats
    return HttpResponse(stats_serializer(stats))
