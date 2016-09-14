from django.core import serializers
from django.forms.models import model_to_dict


def stats_serializer(stats):
    stats_list = []
    for entry in stats:
        stats_list.extend(model_to_dict(entry))
    return stats_list
