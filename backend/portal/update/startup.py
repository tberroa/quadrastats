from stats.models import ChampionStats
from stats.models import Match
from stats.models import MatchStats
from stats.models import SeasonStats

from summoners.models import Summoner

from threading import Thread

from .riotapi import match_detail
from .riotapi import match_list

class UpdateThread(Thread):
    def __init__(self, event):
        Thread.__init__(self)
        self.stopped = event

    def run(self):
        while not self.stopped.wait(5):
            print("beginning update")
            update_all()
            print("update complete")

def update_all():
    # get all summoner objects
    summoners = Summoner.objects.all().order_by("modified")

    # update each summoner one at a time
    for summoner in summoners:
        update_one(summoner)

def update_one(summoner):
    # get the summoners match list
    val = match_list(summoner)
    if val[0] != 200:
        # error occurrred, return http response
        return (False, val)
    else:
        matches = val[1]["matches"]

    # slice out the 10 most recent games
    matches = matches[:10]

    for match in matches:
        # get match object, create new if required
        try:
            match_o = Match.objects.get(region = summoner.region, riot_id = match["matchId"])
        except Match.DoesNotExist:
            match_o = Match.objects.create(region = summoner.region, riot_id = match["matchId"])

        # record stats if required
        try:
            MatchStats.objects.get(summoner = summoner, match = match_o)
        except MatchStats.DoesNotExist:
            # get match detail
            val = match_detail(match, summoner.region)
            if val[0] != 200:
                # error occurred, return http response
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
                # identity info			
                summoner = summoner, \
                match = match_o, \
                champion = info["championId"], \
                lane = info["timeline"]["lane"], \
                role = info["timeline"]["role"], \

                # stats
                assists = info["stats"]["assists"], \
                champ_level = info["stats"]["champLevel"], \
                deaths = info["stats"]["deaths"], \
                double_kills = info["stats"]["doubleKills"], \
                first_blood_assist = info["stats"]["firstBloodAssist"], \
                first_blood_kill = info["stats"]["firstBloodKill"], \
                first_inhibitor_assist = info["stats"]["firstInhibitorAssist"], \
                first_inhibitor_kill = info["stats"]["firstInhibitorKill"], \
                first_tower_assist = info["stats"]["firstTowerAssist"], \
                first_tower_kill = info["stats"]["firstTowerKill"], \
                gold_earned = info["stats"]["goldEarned"], \
                gold_spent = info["stats"]["goldSpent"], \
                inhibitor_kills = info["stats"]["inhibitorKills"], \
                item0 = info["stats"]["item0"], \
                item1 = info["stats"]["item1"], \
                item2 = info["stats"]["item2"], \
                item3 = info["stats"]["item3"], \
                item4 = info["stats"]["item4"], \
                item5 = info["stats"]["item5"], \
                item6 = info["stats"]["item6"], \
                killing_sprees = info["stats"]["killingSprees"], \
                kills = info["stats"]["kills"], \
                largest_critical_strike = info["stats"]["largestCriticalStrike"], \
                largest_killing_spree = info["stats"]["largestKillingSpree"], \
                largest_multi_kill = info["stats"]["largestMultiKill"], \
                magic_damage_dealt = info["stats"]["magicDamageDealt"], \
                magic_damage_dealt_to_champions = info["stats"]["magicDamageDealtToChampions"], \
                magic_damage_taken = info["stats"]["magicDamageTaken"], \
                minions_killed = info["stats"]["minionsKilled"], \
                neutral_minions_killed = info["stats"]["neutralMinionsKilled"], \
                neutral_minions_killed_enemy_jungle = info["stats"]["neutralMinionsKilledEnemyJungle"], \
                neutral_minions_killed_team_jungle = info["stats"]["neutralMinionsKilledTeamJungle"], \
                penta_kills = info["stats"]["pentaKills"], \
                physical_damage_dealt = info["stats"]["physicalDamageDealt"], \
                physical_damage_dealt_to_champions = info["stats"]["physicalDamageDealtToChampions"], \
                physical_damage_taken = info["stats"]["physicalDamageTaken"], \
                quadra_kills = info["stats"]["quadraKills"], \
                sight_wards_bought_in_game = info["stats"]["sightWardsBoughtInGame"], \
                total_damage_dealt = info["stats"]["totalDamageDealt"], \
                total_damage_dealt_to_champions = info["stats"]["totalDamageDealtToChampions"], \
                total_damage_taken = info["stats"]["totalDamageTaken"], \
                total_heal = info["stats"]["totalHeal"], \
                total_time_crowd_control_dealt = info["stats"]["totalTimeCrowdControlDealt"], \
                total_units_healed = info["stats"]["totalUnitsHealed"], \
                tower_kills = info["stats"]["towerKills"], \
                triple_kills = info["stats"]["tripleKills"], \
                true_damage_dealt = info["stats"]["trueDamageDealt"], \
                true_damage_dealt_to_champions = info["stats"]["trueDamageDealtToChampions"], \
                true_damage_taken = info["stats"]["trueDamageTaken"], \
                unreal_kills = info["stats"]["unrealKills"], \
                vision_wards_bought_in_game = info["stats"]["visionWardsBoughtInGame"], \
                wards_killed = info["stats"]["wardsKilled"], \
                wards_placed = info["stats"]["wardsPlaced"], \
                winner = info["stats"]["winner"])

    # successful return
    return (True, None)


            

