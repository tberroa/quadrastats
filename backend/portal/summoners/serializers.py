from rest_framework import serializers

from .models import Summoner
from .models import User

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, validated_data):
        return User.objects.create(**validated_data)

class SummonerSerializer(serializers.ModelSerializer):
    user = UserSerializer(required=False)

    class Meta:
        model = Summoner
        fields = '__all__'

    def create(self, validated_data):
        user_data = validated_data.pop("user")
        user = User.objects.create(**user_data)
        return Summoner.objects.create(user = user, **validated_data)
