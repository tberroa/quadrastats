from rest_framework import serializers

from summoners.serializers import SummonerSerializer

from .models import Match
from .models import MatchStats

class MatchSerializer(serializers.ModelSerializer):
    class Meta:
        model = Match
        fields = '__all__'

class MatchStatsSerializer(serializers.ModelSerializer):
    summoner = SummonerSerializer()
    match = MatchSerializer()

    class Meta:
        model = MatchStats
        fields = '__all__'
