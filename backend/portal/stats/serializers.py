from rest_framework import serializers

from .models import MatchStats
from .models import SeasonStats

class MatchStatsSerializer(serializers.ModelSerializer):
    class Meta:
        model = MatchStats
        fields = '__all__'

class SeasonStatsSerializer(serializers.ModelSerializer):
    class Meta:
        model = SeasonStats
        fields = '__all__'
