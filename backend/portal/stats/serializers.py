from rest_framework import serializers
from stats.models import MatchStats
from stats.models import SeasonStats


class MatchStatsSerializer(serializers.ModelSerializer):
    class Meta:
        model = MatchStats
        fields = '__all__'


class SeasonStatsSerializer(serializers.ModelSerializer):
    class Meta:
        model = SeasonStats
        fields = '__all__'
