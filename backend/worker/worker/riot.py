import json
import string
from cassiopeia import baseriotapi
from cassiopeia.type.api.exception import APIError
from django.core.cache import cache
from django.db.models import Q
from django.db.utils import IntegrityError
from django.http import HttpResponse
from django.views.decorators.http import require_POST
from worker.keys import RIOT_API_KEY
from stats.models import MatchStats
from stats.models import SeasonStats
from summoners.models import Summoner

QUEUE = "TEAM_BUILDER_DRAFT_RANKED_5x5"
SEASON = "SEASON2016"
baseriotapi.set_api_key(RIOT_API_KEY)
baseriotapi.set_rate_limits((270, 10), (16200, 600))


def format_key(key):
    # filter to remove punctuation
    translator = str.maketrans({key: None for key in string.punctuation})

    # also remove white spaces and make lowercase
    return key.translate(translator).replace(" ", "").lower()


def is_keystone(mastery_id):
    if mastery_id == 6161:
        return True
    elif mastery_id == 6162:
        return True
    elif mastery_id == 6164:
        return True
    elif mastery_id == 6361:
        return True
    elif mastery_id == 6362:
        return True
    elif mastery_id == 6363:
        return True
    elif mastery_id == 6261:
        return True
    elif mastery_id == 6262:
        return True
    elif mastery_id == 6263:
        return True
    return False


def riot_request(region, args):
    # set region
    baseriotapi.set_region(region)

    # extract arguments
    request = args.get("request")
    key = args.get("key")
    match_id = args.get("match_id")
    summoner_id = args.get("summoner_id")
    summoner_ids = args.get("summoner_ids")

    # make request
    if request == 1:
        riot_response = baseriotapi.get_summoners_by_name(format_key(key))
    elif request == 2:
        riot_response = baseriotapi.get_match_list(summoner_id, 0, 0, 0, 0, 0, QUEUE, SEASON)
    elif request == 3:
        riot_response = baseriotapi.get_match(match_id)
    elif request == 4:
        riot_response = baseriotapi.get_leagues_by_summoner(summoner_ids)
    elif request == 5:
        riot_response = baseriotapi.get_ranked_stats(summoner_id, SEASON)
    elif request == 6:
        riot_response = baseriotapi.get_summoner_runes(summoner_id)
    else:
        riot_response = None

    # return response
    return riot_response

@require_POST
def update(request):
    # extract data
    data = json.loads(request.body.decode('utf-8'))
    region = data.get("region")
    key = data.get("key")

    # ensure the data is valid
    if None in (region, key):
        return HttpResponse(status=400)

    # ensure proper key format
    key = format_key(key)

    try:
        # get summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
    except Summoner.DoesNotExist:
        return HttpResponse(status=400)

    # update the summoners league stats
    update_league(region, str(summoner_o.summoner_id))

    # update the summoners match stats
    update_match(summoner_o)

    # update the summoners season stats
    update_season(summoner_o)

    try:
        # get updated summoner object
        summoner_o = Summoner.objects.get(region=region, key=key)
    except Summoner.DoesNotExist:
        return HttpResponse(status=400)

    # successful return
    return HttpResponse(status=200)


@require_POST
def update_all(request):
    # get the 100 most recently accessed summoners
    summoners_o = list(Summoner.objects.all().order_by("-accessed")[:100])

    # initialize dictionary required for updating league information
    summoner_ids_dict = dict()

    # populate dictionary
    for summoner_o in summoners_o:
        # check if this is the first summoner for the region
        if summoner_ids_dict.get(summoner_o.region) is None:
            # initialize a list which will hold multiple lists
            summoner_ids_list = []

            # initialize the first list within the list of lists
            summoner_ids = [str(summoner_o.summoner_id)]

            # insert the list into the list of lists
            summoner_ids_list.append(summoner_ids)

            # insert the list of lists into the dictionary
            summoner_ids_dict[summoner_o.region] = summoner_ids_list
        else:
            # get the list of lists of summoner ids for this region
            summoner_ids_list = summoner_ids_dict.get(summoner_o.region)

            # iterate over the list of lists looking for a spot to insert the current id
            inserted = False
            for summoner_ids in summoner_ids_list:
                if len(summoner_ids) < 10:
                    summoner_ids.append(str(summoner_o.summoner_id))
                    inserted = True

            # if a spot wasn't found, create a new list
            if not inserted:
                summoner_ids = [str(summoner_o.summoner_id)]
                summoner_ids_list.append(summoner_ids)

    # update league information
    for region in summoner_ids_dict:
        summoner_ids_list = summoner_ids_dict.get(region)
        for summoner_ids in summoner_ids_list:
            summoner_ids = ",".join(summoner_ids)
            update_league(region, summoner_ids)

    # update match and season stats
    for summoner_o in summoners_o:
        update_match(summoner_o)
        update_season(summoner_o)

    # successful return
    return HttpResponse(status=200)


def update_league(region, summoner_ids):
    try:
        # request league information from riot
        args = {"request": 4, "summoner_ids": summoner_ids}
        riot_response = riot_request(region, args)

        # convert the comma separated string into a list
        summoner_ids = summoner_ids.split(",")

        # extract the league data
        leagues_list = []
        for summoner_id in summoner_ids:
            leagues_list.append(riot_response.get(str(summoner_id)))
    except (APIError, AttributeError):
        return False

    # iterate over each summoner's leagues
    for index, leagues in enumerate(leagues_list):
        try:
            # iterate over the leagues looking for the dynamic queue league
            league = None
            for item in leagues:
                if item.queue == "RANKED_SOLO_5x5":
                    league = item

            # ensure the dynamic queue league was found
            if league is None:
                continue

            # iterate over the league entries to get more detailed information
            division, lp, wins, losses, series = None, None, None, None, ""
            for entry in league.entries:
                if entry.playerOrTeamId == summoner_ids[index]:
                    division = entry.division
                    lp = entry.leaguePoints
                    wins = entry.wins
                    losses = entry.losses
                    if entry.miniSeries is not None:
                        series = entry.miniSeries.progress

            # update the summoner object
            Summoner.objects.filter(region=region, summoner_id=summoner_ids[index]) \
                .update(tier=league.tier,
                        division=division,
                        lp=lp,
                        wins=wins,
                        losses=losses,
                        series=series)

            # cache the updated summoner object
            summoner_o = Summoner.objects.get(region=region, summoner_id=summoner_ids[index])
            cache.set(region + summoner_o.key + "summoner", summoner_o, None)
        except (AttributeError, IntegrityError):
            pass

    # successful return
    return True


def update_match(summoner_o):
    # extract region and summoner id for readability
    region = summoner_o.region
    summoner_id = summoner_o.summoner_id

    try:
        # request match list from riot
        args = {"request": 2, "summoner_id": summoner_id}
        riot_response = riot_request(region, args)

        # extract the matches
        matches = riot_response.matches

        # slice out the 20 most recent games
        matches = matches[:20]

        # iterate over the matches looking for new matches
        match_details = []
        for match in matches:
            if not MatchStats.objects.filter(region=region, summoner_id=summoner_id, match_id=match.matchId).exists():
                args = {"request": 3, "match_id": match.matchId}
                riot_response = riot_request(region, args)
                if riot_response.matchDuration > 600:
                    match_details.append(riot_response)
    except (APIError, AttributeError):
        return False

    # intialize list for new stats
    new_stats = []

    # iterate over the match details
    for match_detail in match_details:
        try:
            # get the summoner's participant id for this match
            participant_id = None
            for identity in match_detail.participantIdentities:
                if identity.player.summonerId == summoner_id:
                    participant_id = identity.participantId

                    # while here, update the summoner's profile icon
                    Summoner.objects.filter(region=region, summoner_id=summoner_id) \
                        .update(profile_icon=identity.player.profileIcon)

            # get the participants from this game
            participants = match_detail.participants

            # get the summoners information for this game
            info = participants[participant_id - 1]
            stats = info.stats
            timeline = info.timeline

            # search for keystone mastery
            keystone = None
            for mastery in info.masteries:
                if is_keystone(mastery.masteryId):
                    keystone = mastery.masteryId

            # calculate team kills, deaths, and assists
            team_kills, team_deaths, team_assists = 0, 0, 0
            if participant_id <= 5:
                x, y = 0, 5
            else:
                x, y = 5, 10
            for i in range(x, y):
                team_kills += participants[i].stats.kills
                team_deaths += participants[i].stats.deaths
                team_assists += participants[i].stats.assists

            try:
                # calculate @10 stats separately because they bug often
                cs_at_ten = float('%.3f' % (timeline.creepsPerMinDeltas.zeroToTen * 10))
                cs_diff_at_ten = float('%.3f' % (timeline.csDiffPerMinDeltas.zeroToTen * 10))
            except AttributeError:
                cs_at_ten, cs_diff_at_ten = None, None

            # calculate other stats
            minutes = match_detail.matchDuration / 60
            cs_per_min = float('%.3f' % ((stats.minionsKilled + stats.neutralMinionsKilled) / minutes))
            dmg_per_min = float('%.3f' % (stats.totalDamageDealtToChampions / minutes))
            gold_per_min = float('%.3f' % (stats.goldEarned / minutes))
            if stats.deaths > 0:
                kda = float('%.3f' % ((stats.kills + stats.assists) / stats.deaths))
            else:
                kda = float('%.3f' % (stats.kills + stats.assists))
            if team_kills > 0:
                kill_participation = float('%.3f' % (((stats.kills + stats.assists) / team_kills) * 100))
            else:
                kill_participation = 0

            # junglers and supports don't cs, set cs stats to none to prevent outliers
            if timeline.lane == "JUNGLE" or timeline.role == "DUO_SUPPORT":
                cs_at_ten = None
                cs_diff_at_ten = None
                cs_per_min = None

            # append match stats object to list of new stats
            new_stats.append(MatchStats(
                # identity info
                region=summoner_o.region,
                summoner_key=summoner_o.key,
                summoner_name=summoner_o.name,
                summoner_id=summoner_o.summoner_id,
                match_id=match_detail.matchId,
                match_creation=match_detail.matchCreation,
                match_duration=match_detail.matchDuration,
                champion=info.championId,
                lane=timeline.lane,
                role=timeline.role,
                spell1=info.spell1Id,
                spell2=info.spell2Id,
                keystone=keystone,

                # raw stats
                assists=stats.assists,
                champ_level=stats.champLevel,
                deaths=stats.deaths,
                double_kills=stats.doubleKills,
                first_blood_assist=stats.firstBloodAssist,
                first_blood_kill=stats.firstBloodKill,
                first_inhibitor_assist=stats.firstInhibitorAssist,
                first_inhibitor_kill=stats.firstInhibitorKill,
                first_tower_assist=stats.firstTowerAssist,
                first_tower_kill=stats.firstTowerKill,
                gold_earned=stats.goldEarned,
                gold_spent=stats.goldSpent,
                inhibitor_kills=stats.inhibitorKills,
                item0=stats.item0,
                item1=stats.item1,
                item2=stats.item2,
                item3=stats.item3,
                item4=stats.item4,
                item5=stats.item5,
                item6=stats.item6,
                killing_sprees=stats.killingSprees,
                kills=stats.kills,
                largest_critical_strike=stats.largestCriticalStrike,
                largest_killing_spree=stats.largestKillingSpree,
                largest_multi_kill=stats.largestMultiKill,
                magic_damage_dealt=stats.magicDamageDealt,
                magic_damage_dealt_to_champions=stats.magicDamageDealtToChampions,
                magic_damage_taken=stats.magicDamageTaken,
                minions_killed=stats.minionsKilled + stats.neutralMinionsKilled,
                neutral_minions_killed=stats.neutralMinionsKilled,
                neutral_minions_killed_enemy_jungle=stats.neutralMinionsKilledEnemyJungle,
                neutral_minions_killed_team_jungle=stats.neutralMinionsKilledTeamJungle,
                penta_kills=stats.pentaKills,
                physical_damage_dealt=stats.physicalDamageDealt,
                physical_damage_dealt_to_champions=stats.physicalDamageDealtToChampions,
                physical_damage_taken=stats.physicalDamageTaken,
                quadra_kills=stats.quadraKills,
                sight_wards_bought_in_game=stats.sightWardsBoughtInGame,
                total_damage_dealt=stats.totalDamageDealt,
                total_damage_dealt_to_champions=stats.totalDamageDealtToChampions,
                total_damage_taken=stats.totalDamageTaken,
                total_heal=stats.totalHeal,
                total_time_crowd_control_dealt=stats.totalTimeCrowdControlDealt,
                total_units_healed=stats.totalUnitsHealed,
                tower_kills=stats.towerKills,
                triple_kills=stats.tripleKills,
                true_damage_dealt=stats.trueDamageDealt,
                true_damage_dealt_to_champions=stats.trueDamageDealtToChampions,
                true_damage_taken=stats.trueDamageTaken,
                unreal_kills=stats.unrealKills,
                vision_wards_bought_in_game=stats.visionWardsBoughtInGame,
                wards_killed=stats.wardsKilled,
                wards_placed=stats.wardsPlaced,
                winner=stats.winner,

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
                team_assists=team_assists))
        except (AttributeError, IntegrityError, TypeError):
            pass

    # create the new stats
    MatchStats.objects.bulk_create(new_stats)

    # collect all stats for this summoner
    query = Q(region=region, summoner_id=summoner_id)
    stats_all = MatchStats.objects.filter(query).order_by("-match_creation")

    # organize into 20 newest and extras
    stats_current = list(stats_all[:20])
    stats_outdated = stats_all[20:]

    # cache the 20 newest stats
    cache.set(region + summoner_o.key + "match", stats_current, None)

    # delete the extras
    for entry in stats_outdated:
        entry.delete()

    # successful return
    return True


def update_season(summoner_o):
    # extract region and summoner id for readability
    region = summoner_o.region
    summoner_id = summoner_o.summoner_id

    try:
        # request season stats from riot
        args = {"request": 5, "summoner_id": summoner_id}
        riot_response = riot_request(region, args)

        # extract the season stats by champions
        stats_by_champion = riot_response.champions
    except (APIError, AttributeError):
        return False

    # intialize list for new stats
    new_stats = []

    # iterate over list of stats
    for entry in stats_by_champion:
        if SeasonStats.objects.filter(region=region, summoner_id=summoner_id, champion=entry.id).exists():
            try:
                # update the stats
                SeasonStats.objects.filter(region=region, summoner_id=summoner_id, champion=entry.id).update(
                    # identity info
                    region=summoner_o.region,
                    summoner_key=summoner_o.key,
                    summoner_name=summoner_o.name,
                    summoner_id=summoner_o.summoner_id,
                    champion=entry.id,

                    # raw stats
                    assists=entry.stats.totalAssists,
                    damage_dealt=entry.stats.totalDamageDealt,
                    damage_taken=entry.stats.totalDamageTaken,
                    deaths=entry.stats.totalDeathsPerSession,
                    double_kills=entry.stats.totalDoubleKills,
                    games=entry.stats.totalSessionsPlayed,
                    gold_earned=entry.stats.totalGoldEarned,
                    kills=entry.stats.totalChampionKills,
                    losses=entry.stats.totalSessionsLost,
                    magic_damage_dealt=entry.stats.totalMagicDamageDealt,
                    max_deaths=entry.stats.maxNumDeaths,
                    max_killing_spree=entry.stats.maxLargestKillingSpree,
                    max_kills=entry.stats.maxChampionsKilled,
                    minion_kills=entry.stats.totalMinionKills,
                    neutral_minion_kills=entry.stats.totalNeutralMinionsKilled,
                    penta_kills=entry.stats.totalPentaKills,
                    physical_damage_dealt=entry.stats.totalPhysicalDamageDealt,
                    quadra_kills=entry.stats.totalQuadraKills,
                    triple_kills=entry.stats.totalTripleKills,
                    wins=entry.stats.totalSessionsWon)
            except (AttributeError, IntegrityError):
                pass
        else:
            try:
                # append entry to list of new stats
                new_stats.append(SeasonStats(
                    # identity info
                    region=summoner_o.region,
                    summoner_key=summoner_o.key,
                    summoner_name=summoner_o.name,
                    summoner_id=summoner_o.summoner_id,
                    champion=entry.id,

                    # raw stats
                    assists=entry.stats.totalAssists,
                    damage_dealt=entry.stats.totalDamageDealt,
                    damage_taken=entry.stats.totalDamageTaken,
                    deaths=entry.stats.totalDeathsPerSession,
                    double_kills=entry.stats.totalDoubleKills,
                    games=entry.stats.totalSessionsPlayed,
                    gold_earned=entry.stats.totalGoldEarned,
                    kills=entry.stats.totalChampionKills,
                    losses=entry.stats.totalSessionsLost,
                    magic_damage_dealt=entry.stats.totalMagicDamageDealt,
                    max_deaths=entry.stats.maxNumDeaths,
                    max_killing_spree=entry.stats.maxLargestKillingSpree,
                    max_kills=entry.stats.maxChampionsKilled,
                    minion_kills=entry.stats.totalMinionKills,
                    neutral_minion_kills=entry.stats.totalNeutralMinionsKilled,
                    penta_kills=entry.stats.totalPentaKills,
                    physical_damage_dealt=entry.stats.totalPhysicalDamageDealt,
                    quadra_kills=entry.stats.totalQuadraKills,
                    triple_kills=entry.stats.totalTripleKills,
                    wins=entry.stats.totalSessionsWon))
            except (AttributeError, IntegrityError):
                pass

    # create the new stats
    SeasonStats.objects.bulk_create(new_stats)

    # collect all the stats found for this summoner
    query = Q(region=region, summoner_id=summoner_id)
    stats_all = list(SeasonStats.objects.filter(query))

    # cache the stats
    cache.set(region + summoner_o.key + "season", stats_all, None)

    # successful return
    return True
