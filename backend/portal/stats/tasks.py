from __future__ import absolute_import

from portal.tasks import riot_request_br
from portal.tasks import riot_request_eune
from portal.tasks import riot_request_euw
from portal.tasks import riot_request_jp
from portal.tasks import riot_request_kr
from portal.tasks import riot_request_lan
from portal.tasks import riot_request_las
from portal.tasks import riot_request_na
from portal.tasks import riot_request_oce
from portal.tasks import riot_request_ru
from portal.tasks import riot_request_tr
from stats.models import MatchStats
from stats.models import SeasonStats
from summoners.models import Summoner

from celery import shared_task


# The Riot api can potentially return None for any field.
# Due to this, many None checks are in place.

def is_keystone(mastery_id):
    if mastery_id == 6161:
        return True
    if mastery_id == 6162:
        return True
    if mastery_id == 6164:
        return True
    if mastery_id == 6361:
        return True
    if mastery_id == 6362:
        return True
    if mastery_id == 6363:
        return True
    if mastery_id == 6261:
        return True
    if mastery_id == 6262:
        return True
    if mastery_id == 6263:
        return True
    return False


@shared_task
def update_all():
    # get all summoner objects
    summoners = Summoner.objects.all().order_by("modified")

    # update each summoner
    for summoner in summoners:
        update_one(summoner)

    # successful return
    return True, None


def update_one(summoner):
    # extract required data
    region = summoner.region
    summoner_id = summoner.summoner_id

    # create match list request argument
    args = {"request": 2, "summoner_id": summoner_id}

    # chain tasks together
    if region == "br":
        chain = riot_request_br.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "eune":
        chain = riot_request_eune.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "euw":
        chain = riot_request_euw.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "jp":
        chain = riot_request_jp.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "kr":
        chain = riot_request_kr.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "lan":
        chain = riot_request_lan.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "las":
        chain = riot_request_las.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "na":
        chain = riot_request_na.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "oce":
        chain = riot_request_oce.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "ru":
        chain = riot_request_ru.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()
    if region == "tr":
        chain = riot_request_tr.s(args) | process_match_list.s(summoner_id) | get_match_details.s(summoner)
        chain()

    # successful return
    return True, None


@shared_task
def process_match_list(val, summoner_id):
    # check the Riot request response for error
    if val[0] != 200:
        return False, val

    # extract the matches from the Riot request response
    matches = val[1].get("matches")

    # defensive check
    if matches is None:
        return False, None

    # initialize list of match detail request arguments
    args_list = []

    # slice out the 20 most recent games
    matches = matches[:20]

    # iterate over each match
    for match in matches:
        match_id = match.get("matchId")

        # defensive check
        if match_id is None:
            return False, None

        try:
            # check if the match is already in database
            MatchStats.objects.get(summoner_id=summoner_id, match_id=match_id)
        except MatchStats.DoesNotExist:
            # match not in database, create match detail request argument
            args = {"request": 3, "match_id": match_id}

            # append it to the list
            args_list.append(args)

    # return the list of match detail request arguments
    return True, args_list


@shared_task
def get_match_details(val, summoner):
    # part of chain, make sure the process match list task returned successfully
    if not val[0]:
        return False, val[1]

    # extract the list of match detail request arguments
    args_list = val[1]

    # extract region
    region = summoner.region

    # iterate over each matches list of request arguments
    for args in args_list:
        # extract match id
        match_id = args_list.get("match_id")

        # chain tasks together
        if region == "br":
            chain = riot_request_br.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "eune":
            chain = riot_request_eune.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "euw":
            chain = riot_request_euw.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "jp":
            chain = riot_request_jp.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "kr":
            chain = riot_request_kr.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "lan":
            chain = riot_request_lan.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "las":
            chain = riot_request_las.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "na":
            chain = riot_request_na.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "oce":
            chain = riot_request_oce.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "ru":
            chain = riot_request_ru.s(args) | process_match_details.s(summoner, match_id)
            chain()
        if region == "tr":
            chain = riot_request_tr.s(args) | process_match_details.s(summoner, match_id)
            chain()

    # successful return
    return True, None


@shared_task
def process_match_details(val, summoner, match_id):
    # check the Riot request response for error
    if val[0] != 200:
        return False, val

    # extract the match details from the Riot request response
    detail = val[1]

    # defensive check
    if detail is None:
        return False, None

    # get creation time
    match_creation = detail.get("matchCreation")

    # defensive check
    if match_creation is None:
        return False, None

    # get the participant identities
    identities = detail.get("participantIdentities")

    # defensive check
    if identities is None:
        return False, None

    # get the summoners participant id for this match
    participant_id = None
    for identity in identities:
        player = identity.get("player")

        # defensive check
        if player is None:
            return False, None

        if player.get("summonerId") == summoner.summoner_id:
            participant_id = identity.get("participantId")

            # defensive check
            if participant_id is None:
                return False, None

            # while here, update summoner profile icon
            profile_icon = player.get("profileIcon")
            if profile_icon is not None:
                summoner.profile_icon = profile_icon
                summoner.save()

    # defensive check, the summoners id could have been None
    if participant_id is None:
        return False, None

    # get the participants from this game
    participants = detail.get("participants")

    # defensive check
    if participants is None:
        return False, None

    # get the summoners information for this game
    info = participants[participant_id - 1]
    spell1 = info.get("spell1Id")
    spell2 = info.get("spell2Id")
    stats = info.get("stats")
    timeline = info.get("timeline")

    # defensive check
    if None in (spell1, spell2, stats, timeline):
        return False, None

    # search for keystone mastery
    keystone = None
    masteries = info.get("masteries")
    for mastery in masteries:
        if is_keystone(mastery.get("masteryId")):
            keystone = mastery.get("masteryId")

    # champion, lane, and role cannot be null
    champion = info.get("championId")
    lane = timeline.get("lane")
    role = timeline.get("role")

    # defensive checks
    if None in (champion, lane, role):
        return False, None

    # gather important raw stats required for advanced stat calculation
    assists = stats.get("assists")
    cs_diff_per_min_deltas = timeline.get("csDiffPerMinDeltas")
    cs_per_min_deltas = timeline.get("creepsPerMinDeltas")
    deaths = stats.get("deaths")
    gold_earned = stats.get("goldEarned")
    kills = stats.get("kills")
    match_duration = detail.get("matchDuration")
    minions_killed = stats.get("minionsKilled")
    neutral_minions_killed = stats.get("neutralMinionsKilled")
    total_dmg_to_champs = stats.get(
        "totalDamageDealtToChampions")  # getting team kills, deaths, and assists is a bit more involved
    team_kills = 0
    team_deaths = 0
    team_assists = 0
    if participant_id <= 5:
        x = 0
        y = 5
    else:
        x = 5
        y = 10
    for i in range(x, y):
        participant_stats = participants[i].get("stats")
        # defensive check
        if participant_stats is None:
            return False, None
        participant_kills = participant_stats.get("kills")
        participant_deaths = participant_stats.get("deaths")
        participant_assists = participant_stats.get("assists")
        # defensive check
        if None in (participant_kills, participant_deaths, participant_assists):
            return False, None
        team_kills = team_kills + participant_kills
        team_deaths = team_deaths + participant_deaths
        team_assists = team_assists + participant_assists

    # perform checks on the critical stats
    if None in (assists, deaths, gold_earned, kills, match_duration, minions_killed,
                neutral_minions_killed, total_dmg_to_champs):
        return False, None

    # its okay for the @10 stats to be None, they bug often
    if cs_per_min_deltas is not None:
        cs_rate = cs_per_min_deltas.get("zeroToTen")
        if cs_rate is not None:
            cs_at_ten = float('%.3f' % (cs_rate * 10))
        else:
            cs_at_ten = None
    else:
        cs_at_ten = None
    if cs_diff_per_min_deltas is not None:
        cs_diff_rate = cs_diff_per_min_deltas.get("zeroToTen")
        if cs_diff_rate is not None:
            cs_diff_at_ten = float('%.3f' % (cs_diff_rate * 10))
        else:
            cs_diff_at_ten = None
    else:
        cs_diff_at_ten = None

    # calculate the other stats
    minutes = match_duration / 60
    cs_per_min = float('%.3f' % ((minions_killed + neutral_minions_killed) / minutes))
    dmg_per_min = float('%.3f' % (total_dmg_to_champs / minutes))
    gold_per_min = float('%.3f' % (gold_earned / minutes))
    if deaths > 0:
        kda = float('%.3f' % ((kills + assists) / deaths))
    else:
        kda = float('%.3f' % (kills + assists))
    if team_kills > 0:
        kill_participation = float('%.3f' % (((kills + assists) / team_kills) * 100))
    else:
        kill_participation = 0

    # junglers and supports don't cs, set cs stats to None to prevent outliers
    if lane == "JUNGLE" or role == "DUO_SUPPORT":
        cs_at_ten = None
        cs_diff_at_ten = None
        cs_per_min = None

    # everything looks good, time to create a new match stats object
    # important to remember: non critical stats can be None
    MatchStats.objects.create(
        # identity info
        region=summoner.region,
        summoner_key=summoner.key,
        summoner_name=summoner.name,
        summoner_id=summoner.summoner_id,
        match_id=match_id,
        match_creation=match_creation,
        match_duration=match_duration,
        champion=champion,
        lane=lane,
        role=role,
        spell1=spell1,
        spell2=spell2,
        keystone=keystone,
        # raw stats
        assists=assists,
        champ_level=stats.get("champLevel"),
        deaths=deaths,
        double_kills=stats.get("doubleKills"),
        first_blood_assist=stats.get("firstBloodAssist"),
        first_blood_kill=stats.get("firstBloodKill"),
        first_inhibitor_assist=stats.get("firstInhibitorAssist"),
        first_inhibitor_kill=stats.get("firstInhibitorKill"),
        first_tower_assist=stats.get("firstTowerAssist"),
        first_tower_kill=stats.get("firstTowerKill"),
        gold_earned=gold_earned,
        gold_spent=stats.get("goldSpent"),
        inhibitor_kills=stats.get("inhibitorKills"),
        item0=stats.get("item0"),
        item1=stats.get("item1"),
        item2=stats.get("item2"),
        item3=stats.get("item3"),
        item4=stats.get("item4"),
        item5=stats.get("item5"),
        item6=stats.get("item6"),
        killing_sprees=stats.get("killingSprees"),
        kills=kills,
        largest_critical_strike=stats.get("largestCriticalStrike"),
        largest_killing_spree=stats.get("largestKillingSpree"),
        largest_multi_kill=stats.get("largestMultiKill"),
        magic_damage_dealt=stats.get("magicDamageDealt"),
        magic_damage_dealt_to_champions=stats.get("magicDamageDealtToChampions"),
        magic_damage_taken=stats.get("magicDamageTaken"),
        minions_killed=minions_killed + neutral_minions_killed,
        neutral_minions_killed=neutral_minions_killed,
        neutral_minions_killed_enemy_jungle=stats.get("neutralMinionsKilledEnemyJungle"),
        neutral_minions_killed_team_jungle=stats.get("neutralMinionsKilledTeamJungle"),
        penta_kills=stats.get("pentaKills"),
        physical_damage_dealt=stats.get("physicalDamageDealt"),
        physical_damage_dealt_to_champions=stats.get("physicalDamageDealtToChampions"),
        physical_damage_taken=stats.get("physicalDamageTaken"),
        quadra_kills=stats.get("quadraKills"),
        sight_wards_bought_in_game=stats.get("sightWardsBoughtInGame"),
        total_damage_dealt=stats.get("totalDamageDealt"),
        total_damage_dealt_to_champions=total_dmg_to_champs,
        total_damage_taken=stats.get("totalDamageTaken"),
        total_heal=stats.get("totalHeal"),
        total_time_crowd_control_dealt=stats.get("totalTimeCrowdControlDealt"),
        total_units_healed=stats.get("totalUnitsHealed"),
        tower_kills=stats.get("towerKills"),
        triple_kills=stats.get("tripleKills"),
        true_damage_dealt=stats.get("trueDamageDealt"),
        true_damage_dealt_to_champions=stats.get("trueDamageDealtToChampions"),
        true_damage_taken=stats.get("trueDamageTaken"),
        unreal_kills=stats.get("unrealKills"),
        vision_wards_bought_in_game=stats.get("visionWardsBoughtInGame"),
        wards_killed=stats.get("wardsKilled"),
        wards_placed=stats.get("wardsPlaced"),
        winner=stats.get("winner"),
        # calculated stats
        cs_at_ten=cs_at_ten,
        cs_diff_at_ten=cs_diff_at_ten,
        cs_per_min=cs_per_min,
        dmg_per_min=dmg_per_min,
        gold_per_min=gold_per_min,
        kda=kda,
        kill_participation=kill_participation,
        team_kills=team_kills,
        team_deaths=team_deaths,
        team_assists=team_assists)

    # successful return
    return True, None