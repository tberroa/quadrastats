import json
import requests

from time import sleep

from .key import riot_api_key

def get_summoner(region, key):
    # ensure key has proper format
    key = key.replace(" ", "").lower()

    # construct url
    url = "https://" + region + ".api.pvp.net/api/lol/" + region \
        + "/v1.4/summoner/by-name/" + key \
        + "?api_key=" + riot_api_key

    # make get request
    sleep(1.1)
    r = requests.get(url)

    # return response
    return (r.status_code, json.loads(r.text))
