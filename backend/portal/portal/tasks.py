from __future__ import absolute_import

import json
import requests
import string
import time
from celery import shared_task
from portal.keys import riot_api_key


def format_key(key):
    # filter to remove punctuation
    translator = str.maketrans({key: None for key in string.punctuation})

    # also remove white spaces and make lowercase
    return key.translate(translator).replace(" ", "").lower()


def request_function(region, args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # construct url
    if request == 1:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v1.4/summoner/by-name/" \
              + format_key(key) \
              + "?api_key=" + riot_api_key
    elif request == 2:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key
    elif request == 3:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key
    elif request == 4:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key
    elif request == 5:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key
    elif request == 6:
        url = "https://" + region + ".api.pvp.net/api/lol/" + region + "/v1.4/summoner/" \
              + str(summoner_id) + "/" \
              + "runes?api_key=" + riot_api_key
    else:
        url = ""

    # make get request
    r = requests.get(url)

    # if status code is 429, attempt again up to four more times
    i = 1
    while r.status_code == 429:
        time.sleep(2)
        r = requests.get(url)
        i += 1
        if i > 4:
            break

    # return response
    return r.status_code, json.loads(r.text)


def riot_request(region, args):
    if region == "br":
        return riot_request_br.delay(args).get()
    if region == "eune":
        return riot_request_eune.delay(args).get()
    if region == "euw":
        return riot_request_euw.delay(args).get()
    if region == "jp":
        return riot_request_jp.delay(args).get()
    if region == "kr":
        return riot_request_kr.delay(args).get()
    if region == "lan":
        return riot_request_lan.delay(args).get()
    if region == "las":
        return riot_request_las.delay(args).get()
    if region == "na":
        return riot_request_na.delay(args).get()
    if region == "oce":
        return riot_request_oce.delay(args).get()
    if region == "ru":
        return riot_request_ru.delay(args).get()
    if region == "tr":
        return riot_request_tr.delay(args).get()


@shared_task(rate_limit="40/m")
def riot_request_br(args):
    return request_function("br", args)


@shared_task(rate_limit="40/m")
def riot_request_eune(args):
    return request_function("eune", args)


@shared_task(rate_limit="40/m")
def riot_request_euw(args):
    return request_function("euw", args)


@shared_task(rate_limit="40/m")
def riot_request_jp(args):
    return request_function("jp", args)


@shared_task(rate_limit="40/m")
def riot_request_kr(args):
    return request_function("kr", args)


@shared_task(rate_limit="40/m")
def riot_request_lan(args):
    return request_function("lan", args)


@shared_task(rate_limit="40/m")
def riot_request_las(args):
    return request_function("las", args)


@shared_task(rate_limit="40/m")
def riot_request_na(args):
    return request_function("na", args)


@shared_task(rate_limit="40/m")
def riot_request_oce(args):
    return request_function("oce", args)


@shared_task(rate_limit="40/m")
def riot_request_ru(args):
    return request_function("ru", args)


@shared_task(rate_limit="40/m")
def riot_request_tr(args):
    return request_function("tr", args)
