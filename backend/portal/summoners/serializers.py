from rest_framework import serializers
from summoners.models import Summoner
from summoners.models import User


class SummonerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Summoner
        fields = '__all__'
        extra_kwargs = {'user': {'write_only': True}}


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'
        extra_kwargs = {'email': {'write_only': True},
                        'password': {'write_only': True}}
