from rest_framework import serializers

from .models import MatchStats

class MatchStatsSerializer(serializers.ModelSerializer):
    class Meta:
        model = MatchStats
        fields = '__all__'
