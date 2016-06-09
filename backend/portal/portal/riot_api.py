import json
import requests
import string

from time import sleep

from .api_keys import riot_api_key

def get_match_detail(region, match_id):
    # construct url
    url = "https://" + region + ".api.pvp.net/api/lol/" + region \
        + "/v2.2/match/" + str(match_id) \
        + "?api_key=" + riot_api_key

    # make get request
    sleep(1.1)
    r = requests.get(url)

    # return response
    return (r.status_code, json.loads(r.text))

def get_match_list(region, summoner_id):
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

def get_summoner(region, key):
    # ensure key has proper format
    key = format_key(key)

    # construct url
    url = "https://" + region + ".api.pvp.net/api/lol/" + region \
        + "/v1.4/summoner/by-name/" + key \
        + "?api_key=" + riot_api_key

    # make get request
    sleep(1.1)
    r = requests.get(url)

    # return response
    return (r.status_code, json.loads(r.text).get(key))

def format_key(key):
    # filter to remove punctuation
    translator = str.maketrans({key: None for key in string.punctuation})

    # also remove white spaces and make lowercase
    return(key.translate(translator).replace(" ", "").lower())







