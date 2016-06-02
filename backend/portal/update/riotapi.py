import json, requests

from .keys import riot_api_key

def match_list(summoner):
    # construct url
    region = summoner.region
    season = "SEASON2016"
    queue = "TEAM_BUILDER_DRAFT_RANKED_5x5"
    riot_id = str(summoner.riot_id)
    url = "https://na.api.pvp.net/api/lol/" + region \
        + "/v2.2/matchlist/by-summoner/" + riot_id \
        + "?rankedQueues=" + queue \
        + "&seasons=" + season \
        + "&api_key=" + riot_api_key

    # make get request
    r = requests.get(url)

    # return response
    return json.loads(r.text)["matches"]

def match_detail(match, region):
    # construct url
    url = "https://na.api.pvp.net/api/lol/" + region \
        + "/v2.2/match/" + str(match["matchId"]) \
        + "?api_key=" + riot_api_key

    # make get request
    r = requests.get(url)

    # return response
    return json.loads(r.text)







