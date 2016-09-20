import json
from django.core.cache import cache
from django.db.models import Q
from django.http import HttpResponse
from django.views.decorators.http import require_POST
from portal.errors import INVALID_REQUEST_FORMAT
from portal.riot import format_key
from stats.models import MatchStats
from stats.models import SeasonStats
from stats.serializers import stats_serializer


@require_POST
def get_match_stats(request):
    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # initialize list of stats to be returned
    stats = []

    # initialize list of keys which were not found in the cache
    keys_extra = []

    # look through the cache first
    for key in keys:
        # ensure proper key format
        key = format_key(key)

        # request stats from cache
        result = cache.get(region + key + "match")

        # evaluate result of cache request
        if result is not None:
            stats += result
        else:
            keys_extra.append(key)

    # look through the database if required
    if keys_extra:
        # turn list of extra keys into list of Q objects
        queries = [Q(region=region, summoner_key=key) for key in keys_extra]

        # take one Q object from the list
        query = queries.pop()

        # or the Q object with the ones remaining in the list
        for item in queries:
            query |= item

        # query the database
        stats_extra = list(MatchStats.objects.filter(query).order_by("-match_creation"))

        # add the stats to the list which will be returned
        stats += stats_extra

        # enter stats into cache by key
        for key in keys_extra:
            stats_key = [x for x in stats_extra if x.summoner_key == key]
            cache.set(region + key + "match", stats_key, None)

    # return the stats
    return HttpResponse(stats_serializer(stats))


@require_POST
def get_season_stats(request):
    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    keys = data.get("keys")

    # ensure the data is valid
    if None in (region, keys):
        return HttpResponse(json.dumps(INVALID_REQUEST_FORMAT))

    # initialize list of stats to be returned
    stats = []

    # initialize list of keys which were not found in the cache
    keys_extra = []

    # look through the cache first
    for key in keys:
        # ensure proper key format
        key = format_key(key)

        # request stats from cache
        result = cache.get(region + key + "season")

        # evaluate result of cache request
        if result is not None:
            stats += result
        else:
            keys_extra.append(key)

    # look through the database if required
    if keys_extra:
        # turn list of extra keys into list of Q objects
        queries = [Q(region=region, summoner_key=key) for key in keys_extra]

        # take one Q object from the list
        query = queries.pop()

        # or the Q object with the ones remaining in the list
        for item in queries:
            query |= item

        # query the database
        stats_extra = list(SeasonStats.objects.filter(query))

        # add the stats to the list which will be returned
        stats += stats_extra

        # enter stats into cache by key
        for key in keys_extra:
            stats_key = [x for x in stats_extra if x.summoner_key == key]
            cache.set(region + key + "season", stats_key, None)

    # return the stats
    return HttpResponse(stats_serializer(stats))
