from __future__ import absolute_import

import json
import requests
import string
import time
from cassiopeia import baseriotapi
from celery import shared_task
from portal.keys import RIOT_API_KEY

QUEUE = "TEAM_BUILDER_DRAFT_RANKED_5x5"

SEASON = "SEASON2016"

def format_key(key):
    # filter to remove punctuation
    translator = str.maketrans({key: None for key in string.punctuation})

    # also remove white spaces and make lowercase
    return key.translate(translator).replace(" ", "").lower()

@shared_task
def riot_request(region, args):
    # set api key
    baseriotapi.set_api_key(RIOT_API_KEY)

    # set region
    baseriotapi.set_region(region)

    # extract arguments
    request = args.get("request")
    key = args.get("key")
    match_id = args.get("match_id")
    summoner_id = args.get("summoner_id")
    summoner_ids = args.get("summoner_ids")

    # make request
    if request == 1:
        riot_response = baseriotapi.get_summoners_by_name(format_key(key))
    elif request == 2:
        riot_response = baseriotapi.get_match_list(summoner_id, 0, 0, 0, 0, 0, QUEUE, SEASON)
    elif request == 3:
        riot_response = baseriotapi.get_match(match_id)
    elif request == 4:
        riot_response = baseriotapi.get_leagues_by_summoner(summoner_ids)
    elif request == 5:
        riot_response = baseriotapi.get_ranked_stats(summoner_id, SEASON)
    elif request == 6:
        riot_response = baseriotapi.get_summoner_runes(summoner_id)

    # return response
    return riot_response
