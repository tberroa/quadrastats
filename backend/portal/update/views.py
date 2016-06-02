from django.shortcuts import render

from multiprocessing.dummy import Pool as ThreadPool

from rest_framework.views import APIView
from rest_framework.response import Response

from summoners.models import Summoner
from stats.models import SeasonStats
from stats.models import ChampionStats
from stats.models import Match
from stats.models import MatchStats

from .riotapi import match_list
from .riotapi import match_detail

def get_match_detail(match_id):
    pass
    #construct url

def update(summoner):
    # get the summoners matchlist
    region = summoner.region
    matches = match_list(summoner)
    for match in matches[:1]:
        # get match details for the 10 most recent matches
        detail = match_detail(match, region)
    return detail

class Command(APIView):
    def post(self, request, format=None):
        # extract command
        command = request.data["command"]
        if command == 'run':
            summoners = Summoner.objects.all()
            return Response(update(summoners[0]))
            

