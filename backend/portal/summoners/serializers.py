from django.core import serializers
from django.forms.models import model_to_dict


def summoner_serializer(summoner, many):
    if many is False:
        serial_summoner = model_to_dict(summoner)
        serial_summoner.pop("user", None)
        return serial_summoner
    elif many is True:
        summoners = []
        for entry in summoner:
            serial_summoner = model_to_dict(entry)
            serial_summoner.pop("user", None)
            stats_list.extend(serial_summoner)
        return summoners
