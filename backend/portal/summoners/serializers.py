import json
from django.core import serializers
from django.forms.models import model_to_dict


def summoner_serializer(summoner, email, many):
    if many is False:
        summoner_dict = model_to_dict(summoner)
        summoner_dict.pop("user", None)
        summoner_dict.pop("accessed", None)
        if email is not None:
            summoner_dict.update({"email": email})
        return json.dumps(summoner_dict)
    elif many is True:
        summoners = "["
        for i, entry in enumerate(summoner):
            summoner_dict = model_to_dict(entry)
            summoner_dict.pop("user", None)
            summoner_dict.pop("accessed", None)
            summoners += json.dumps(summoner_dict)
            if i != len(summoner) - 1:
                summoners += ","
        summoners += "]"
        return summoners
