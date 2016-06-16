from __future__ import absolute_import

from celery import shared_task 
from portal.riot_api import get_match_detail
from portal.riot_api import get_match_list
from summoners.models import Summoner

from .models import ChampionStats
from .models import MatchStats
from .models import SeasonStats

# The riot api can potentially return None for any field.
# Due to this, many None checks are in place.

@shared_task
def update_all():
    # get all summoner objects
    summoners = Summoner.objects.all().order_by("modified")

    # update each summoner one at a time
    for summoner in summoners:
        update_one(summoner)

def update_one(summoner):
    # get the summoners match list
    val = get_match_list(summoner.region, summoner.summoner_id)
    if val[0] != 200:
        # error occurrred, return http response
        return (False, val)
    else:
        matches = val[1].get("matches")

    # defensive check
    if matches is None:
        return (False, None)

    # slice out the 10 most recent games
    matches = matches[:10]

    for match in matches:
        match_id = match.get("matchId")

        # defensive check
        if match_id is None:
            return (False, None)

        # record stats if required
        try:
            MatchStats.objects.get(summoner_id = summoner.summoner_id, match_id = match_id)
        except MatchStats.DoesNotExist:
            # get match detail
            val = get_match_detail(summoner.region, match_id)
            if val[0] != 200:
                # error occurred, return http response
                return (False, val)
            else:
                detail = val[1]

            # defensive check
            if detail is None:
                return (False, None)

            # get creation time
            match_creation = detail.get("matchCreation")

            # defensive check
            if match_creation is None:
                return (False, None)

            # get the participant identities
            identities = detail.get("participantIdentities")

            # defensive check
            if identities is None:
                return (False, None)

            # get the summoners participant id for this match
            participant_id = None
            for identity in identities:
                player = identity.get("player")

                # defensive check
                if player is None:
                    return (False, None)

                if player.get("summonerId") == summoner.summoner_id:
                    participant_id = identity.get("participantId")

                    # defensive check
                    if participant_id is None:
                        return (False, None)

                    # while here, update summoner profile icon
                    profile_icon = player.get("profileIcon")
                    if profile_icon is not None:
                        summoner.profile_icon = profile_icon
                        summoner.save()

            # defensive check, the summoners id could have been None
            if participant_id is None:
                return (False, None)

            # get the participants from this game
            participants = detail.get("participants")

            # defensive check
            if participants is None:
                return (False, None)

            # get the summoners information for this game
            info = participants[participant_id-1]
            stats = info.get("stats")
            timeline = info.get("timeline")

            # defensive check
            if None in (stats, timeline):
                return (False, None)

            # champion, lane, and role cannot be null
            champion = info.get("championId")
            lane = timeline.get("lane")
            role = timeline.get("role")

            # defensive checks
            if None in (champion, lane, role):
                return (False, None)

            # gather important raw stats required for advanced stat calculation
            assists = stats.get("assists")
            cs_diff_per_min_deltas = timeline.get("csDiffPerMinDeltas")
            cs_per_min_deltas = timeline.get("creepsPerMinDeltas")
            deaths = stats.get("deaths");
            gold_earned = stats.get("goldEarned")
            kills = stats.get("kills")
            match_duration = detail.get("matchDuration")
            minions_killed = stats.get("minionsKilled")
            neutral_minions_killed = stats.get("neutralMinionsKilled")
            total_dmg_to_champs = stats.get("totalDamageDealtToChampions")

            # getting total team kills is a bit more involved
            total_team_kills = 0
            if participant_id <= 5:
                x = 0; y = 5;
            else:
                x = 5; y = 10;
            for i in range(x,y):
                 participant_stats = participants[i].get("stats")
                 # defensive check
                 if participant_stats is None:
                     return (False, None)
                 participant_kills = participant_stats.get("kills")
                 # defensive check
                 if participant_kills is None:
                     return (False, None)
                 total_team_kills = total_team_kills + participant_kills

            # perform checks on the critical stats
            if None in (assists, deaths, gold_earned, kills, match_duration, minions_killed, \
                        neutral_minions_killed, total_dmg_to_champs):
                return (False, None)

            # its okay for the @10 stats to be None, they bug often
            if cs_per_min_deltas is not None:
                cs_rate = cs_per_min_deltas.get("zeroToTen")
                if cs_rate is not None:
                    cs_at_ten = float('%.3f'%(cs_rate * 10))
                else:
                    cs_at_ten = None
            else:
                cs_at_ten = None
            if cs_diff_per_min_deltas is not None:
                cs_diff_rate = cs_diff_per_min_deltas.get("zeroToTen")
                if cs_diff_rate is not None:
                    cs_diff_at_ten = float('%.3f'%(cs_diff_rate * 10))
                else:
                    cs_diff_at_ten = None
            else:
                cs_diff_at_ten = None

            # calculate the other stats
            minutes = match_duration / 60
            cs_per_min = float('%.3f'%((minions_killed + neutral_minions_killed) / minutes))
            dmg_per_min = float('%.3f'%(total_dmg_to_champs / minutes))
            gold_per_min = float('%.3f'%(gold_earned / minutes))
            if deaths != 0:
                kda = float('%.3f'%((kills + assists) / deaths))
            else:
                kda = float('%.3f'%(kills + assists))
            kill_participation = float('%.3f'%(((kills + assists) / total_team_kills) * 100))

            # junglers and supports don't cs, set cs stats to None to prevent outliers
            if lane == "JUNGLE" or role == "DUO_SUPPORT":
                cs_at_ten = None
                cs_diff_at_ten = None
                cs_per_min = None
                
            # everything looks good, time to create a new match stats object
            # important to remember: non critical stats can be None
            match_stats = MatchStats.objects.create( \
                # identity info	
                region = summoner.region, \
                summoner_name = summoner.name, \
                summoner_id = summoner.summoner_id, \
                match_id = match_id, \
                match_creation = match_creation, \
                match_duration = match_duration, \
                champion = champion, \
                lane = lane, \
                role = role, \

                # raw stats
                assists = assists, \
                champ_level = stats.get("champLevel"), \
                deaths = deaths, \
                double_kills = stats.get("doubleKills"), \
                first_blood_assist = stats.get("firstBloodAssist"), \
                first_blood_kill = stats.get("firstBloodKill"), \
                first_inhibitor_assist = stats.get("firstInhibitorAssist"), \
                first_inhibitor_kill = stats.get("firstInhibitorKill"), \
                first_tower_assist = stats.get("firstTowerAssist"), \
                first_tower_kill = stats.get("firstTowerKill"), \
                gold_earned = gold_earned, \
                gold_spent = stats.get("goldSpent"), \
                inhibitor_kills = stats.get("inhibitorKills"), \
                item0 = stats.get("item0"), \
                item1 = stats.get("item1"), \
                item2 = stats.get("item2"), \
                item3 = stats.get("item3"), \
                item4 = stats.get("item4"), \
                item5 = stats.get("item5"), \
                item6 = stats.get("item6"), \
                killing_sprees = stats.get("killingSprees"), \
                kills = kills, \
                largest_critical_strike = stats.get("largestCriticalStrike"), \
                largest_killing_spree = stats.get("largestKillingSpree"), \
                largest_multi_kill = stats.get("largestMultiKill"), \
                magic_damage_dealt = stats.get("magicDamageDealt"), \
                magic_damage_dealt_to_champions = stats.get("magicDamageDealtToChampions"), \
                magic_damage_taken = stats.get("magicDamageTaken"), \
                minions_killed = minions_killed, \
                neutral_minions_killed = neutral_minions_killed, \
                neutral_minions_killed_enemy_jungle = stats.get("neutralMinionsKilledEnemyJungle"), \
                neutral_minions_killed_team_jungle = stats.get("neutralMinionsKilledTeamJungle"), \
                penta_kills = stats.get("pentaKills"), \
                physical_damage_dealt = stats.get("physicalDamageDealt"), \
                physical_damage_dealt_to_champions = stats.get("physicalDamageDealtToChampions"), \
                physical_damage_taken = stats.get("physicalDamageTaken"), \
                quadra_kills = stats.get("quadraKills"), \
                sight_wards_bought_in_game = stats.get("sightWardsBoughtInGame"), \
                total_damage_dealt = stats.get("totalDamageDealt"), \
                total_damage_dealt_to_champions = total_dmg_to_champs, \
                total_damage_taken = stats.get("totalDamageTaken"), \
                total_heal = stats.get("totalHeal"), \
                total_time_crowd_control_dealt = stats.get("totalTimeCrowdControlDealt"), \
                total_units_healed = stats.get("totalUnitsHealed"), \
                tower_kills = stats.get("towerKills"), \
                triple_kills = stats.get("tripleKills"), \
                true_damage_dealt = stats.get("trueDamageDealt"), \
                true_damage_dealt_to_champions = stats.get("trueDamageDealtToChampions"), \
                true_damage_taken = stats.get("trueDamageTaken"), \
                unreal_kills = stats.get("unrealKills"), \
                vision_wards_bought_in_game = stats.get("visionWardsBoughtInGame"), \
                wards_killed = stats.get("wardsKilled"), \
                wards_placed = stats.get("wardsPlaced"), \
                winner = stats.get("winner"), \

                # calculated stats
                cs_at_ten = cs_at_ten, \
                cs_diff_at_ten = cs_diff_at_ten, \
                cs_per_min = cs_per_min, \
                dmg_per_min = dmg_per_min, \
                gold_per_min = gold_per_min, \
                kda = kda, \
                kill_participation = kill_participation)

    # successful return
    return (True, None)


            

