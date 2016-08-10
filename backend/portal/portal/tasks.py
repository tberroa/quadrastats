from __future__ import absolute_import

import json
import requests
import string
from celery import shared_task
from portal.keys import riot_api_key


def format_key(key):
    # filter to remove punctuation
    translator = str.maketrans({key: None for key in string.punctuation})

    # also remove white spaces and make lowercase
    return key.translate(translator).replace(" ", "").lower()


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


@shared_task(rate_limit="50/m")
def riot_request_br(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://br.api.pvp.net/api/lol/br/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://br.api.pvp.net/api/lol/br/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://br.api.pvp.net/api/lol/br/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://br.api.pvp.net/api/lol/br/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://br.api.pvp.net/api/lol/br/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_eune(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://eune.api.pvp.net/api/lol/eune/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://eune.api.pvp.net/api/lol/eune/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://eune.api.pvp.net/api/lol/eune/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://eune.api.pvp.net/api/lol/eune/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://eune.api.pvp.net/api/lol/eune/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_euw(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://euw.api.pvp.net/api/lol/euw/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://euw.api.pvp.net/api/lol/euw/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://euw.api.pvp.net/api/lol/euw/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://euw.api.pvp.net/api/lol/euw/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://euw.api.pvp.net/api/lol/euw/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_jp(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://jp.api.pvp.net/api/lol/jp/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://jp.api.pvp.net/api/lol/jp/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://jp.api.pvp.net/api/lol/jp/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://jp.api.pvp.net/api/lol/jp/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://jp.api.pvp.net/api/lol/jp/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_kr(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://kr.api.pvp.net/api/lol/kr/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://kr.api.pvp.net/api/lol/kr/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://kr.api.pvp.net/api/lol/kr/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://kr.api.pvp.net/api/lol/kr/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://kr.api.pvp.net/api/lol/kr/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_lan(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://lan.api.pvp.net/api/lol/lan/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://lan.api.pvp.net/api/lol/lan/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://lan.api.pvp.net/api/lol/lan/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://lan.api.pvp.net/api/lol/lan/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://lan.api.pvp.net/api/lol/lan/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_las(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://las.api.pvp.net/api/lol/las/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://las.api.pvp.net/api/lol/las/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://las.api.pvp.net/api/lol/las/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://las.api.pvp.net/api/lol/las/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://las.api.pvp.net/api/lol/las/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_na(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://na.api.pvp.net/api/lol/na/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://na.api.pvp.net/api/lol/na/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://na.api.pvp.net/api/lol/na/v2.5/league/by-summoner/" \
              + summoner_ids \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://na.api.pvp.net/api/lol/na/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_oce(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://oce.api.pvp.net/api/lol/oce/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://oce.api.pvp.net/api/lol/oce/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://oce.api.pvp.net/api/lol/oce/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://oce.api.pvp.net/api/lol/oce/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://oce.api.pvp.net/api/lol/oce/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_ru(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://ru.api.pvp.net/api/lol/ru/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://ru.api.pvp.net/api/lol/ru/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://ru.api.pvp.net/api/lol/ru/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://ru.api.pvp.net/api/lol/ru/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://ru.api.pvp.net/api/lol/ru/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)


@shared_task(rate_limit="50/m")
def riot_request_tr(args):
    # extract arguments
    request = args.get("request")
    key = args.get("key")
    summoner_id = args.get("summoner_id")
    match_id = args.get("match_id")
    summoner_ids = args.get("summoner_ids")

    # get summoner
    if request == 1:
        # ensure key has proper format
        key = format_key(key)

        # construct url
        url = "https://tr.api.pvp.net/api/lol/tr/v1.4/summoner/by-name/" \
              + key \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text).get(key)

    # get match list
    if request == 2:
        # construct url
        url = "https://tr.api.pvp.net/api/lol/tr/v2.2/matchlist/by-summoner/" \
              + str(summoner_id) \
              + "?rankedQueues=TEAM_BUILDER_DRAFT_RANKED_5x5" \
              + "&seasons=SEASON2016" \
              + "&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get match detail
    if request == 3:
        # construct url
        url = "https://tr.api.pvp.net/api/lol/tr/v2.2/match/" \
              + str(match_id) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get league
    if request == 4:
        # construct url
        url = "https://tr.api.pvp.net/api/lol/tr/v2.5/league/by-summoner/" \
              + str(summoner_ids) \
              + "?api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)

    # get season stats
    if request == 5:
        # construct url
        url = "https://tr.api.pvp.net/api/lol/tr/v1.3/stats/by-summoner/" \
              + str(summoner_id) + "/" \
              + "ranked?season=SEASON2016&api_key=" + riot_api_key

        # make get request
        r = requests.get(url)

        # return response
        return r.status_code, json.loads(r.text)
