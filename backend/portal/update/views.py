from django.shortcuts import render

from multiprocessing.dummy import Pool as ThreadPool

from rest_framework.views import APIView
from rest_framework.response import Response

from stats.models import ChampionStats
from stats.models import Match
from stats.models import MatchStats
from stats.models import SeasonStats

from summoners.models import Summoner

from .riotapi import match_detail
from .riotapi import match_list

def update(summoner):
    # get the summoners match list
    val = match_list(summoner)
    if val[0] != 200:
        # error occured, return http response
        return (False, val)
    else:
        matches = val[1]["matches"]
        total_games = val[1]["totalGames"]

    # only interested in new games since last update
    new_games = total_games - summoner.total_games
    if new_games == 0:
        # no new games since last update
        return (True, None)
    else:
        # slice out the new games, 10 max
        matches = matches[:new_games][:10]

    # none of the new games have been processed yet
    total_games = total_games - len(matches)

    for match in matches:
        # get match object, create new if required
        try:
            match_o = Match.objects.get(riot_id = match["matchId"])
        except Match.DoesNotExist:
            match_o = Match.objects.create(riot_id = match["matchId"])

        # record stats if required
        try:
            MatchStats.objects.get(summoner = summoner, match = match_o)
        except MatchStats.DoesNotExist:
            # get match detail
            val = match_detail(match, summoner.region)
            if val[0] != 200:
                # update before returning http response
                summoner.total_games = total_games
                summoner.save()
                return (False, val)
            else:
                detail = val[1]

            # update the match object
            match_o.creation = detail["matchCreation"]
            match_o.duration = detail["matchDuration"]
            match_o.save()

            # get the participant identities
            identities = detail["participantIdentities"]

            # get the summoners participant id for this match
            for identity in identities:
                if identity["player"]["summonerId"] == summoner.riot_id:
                    participant_id = identity["participantId"]
                    # while here, update summoner profile icon
                    summoner.profile_icon = identity["player"]["profileIcon"]
                    summoner.save()

            # get the summoners information for this game
            info = detail["participants"][participant_id-1]

            # create a new match stats object
            match_stats = MatchStats.objects.create( \
                summoner = summoner, \
                match = match_o, \
                champion = info["championId"], \
                lane = info["timeline"]["lane"], \
                role = info["timeline"]["role"], \
                kills = info["stats"]["kills"], \
                deaths = info["stats"]["deaths"], \
                assists = info["stats"]["assists"]) \

        # match fully processed, increment total_games
        total_games += 1

    # update before successful return
    summoner.total_games = total_games
    summoner.save()
    return (True, None)

class Command(APIView):
    def post(self, request, format=None):
        # extract command
        command = request.data["command"]

        if command == 'all':
            # get all summoner objects
            summoners = Summoner.objects.all().order_by("modified")

            # update each summoner one at a time
            for summoner in summoners:
                val = update(summoner)
                
                # check if update failed
                if not val[0]:
                    # return http response
                    return Response(val[1])

            return Response("success")
            

