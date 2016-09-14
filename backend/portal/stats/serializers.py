import json
from django.core import serializers
from django.forms.models import model_to_dict


def stats_serializer(stats):
    stats_list = "["
    for i, entry in enumerate(stats):
        stats_list += json.dumps(model_to_dict(entry))
        if i != len(stats) - 1:
            stats_list += ","
    stats_list += "]"
    return stats_list
