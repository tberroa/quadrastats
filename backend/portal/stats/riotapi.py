import json
import requests

from time import sleep

from .key import riot_api_key

def match_list(region, summoner_id):
    # construct url
    queue = "TEAM_BUILDER_DRAFT_RANKED_5x5"
    season = "SEASON2016"
    url = "https://" + region + ".api.pvp.net/api/lol/" + region \
        + "/v2.2/matchlist/by-summoner/" + str(summoner_id) \
        + "?rankedQueues=" + queue \
        + "&seasons=" + season \
        + "&api_key=" + riot_api_key

    # make get request
    sleep(1.1)
    r = requests.get(url)

    # return response
    return (r.status_code, json.loads(r.text))

def match_detail(region, match_id):
    # construct url
    url = "https://" + region + ".api.pvp.net/api/lol/" + region \
        + "/v2.2/match/" + str(match_id) \
        + "?api_key=" + riot_api_key

    # make get request
    sleep(1.1)
    r = requests.get(url)

    # return response
    return (r.status_code, json.loads(r.text))







