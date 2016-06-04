from django.db import models

from summoners.models import Summoner

class SeasonStats(models.Model):
    class Meta:
        verbose_name_plural = 'Season Stats'
    season = models.CharField(max_length = 128)
    kills = models.IntegerField()
    deaths = models.IntegerField()
    assists = models.IntegerField()
    doublekills = models.IntegerField()
    triplekills = models.IntegerField()
    quadrakills = models.IntegerField()
    pentakills = models.IntegerField()

class ChampionStats(models.Model):
    class Meta:
        verbose_name_plural = 'Champion Stats'
    season = models.CharField(max_length = 128)
    kills = models.IntegerField()
    deaths = models.IntegerField()
    assists = models.IntegerField()
    doublekills = models.IntegerField()
    triplekills = models.IntegerField()
    quadrakills = models.IntegerField()
    pentakills = models.IntegerField()

class Match(models.Model):
    class Meta:
        verbose_name_plural = 'Matches'
    riot_id = models.BigIntegerField()
    creation = models.BigIntegerField(null = True, blank = True)
    duration = models.BigIntegerField(null = True, blank = True)

    def __str__(self):
        return str(self.riot_id)

class MatchStats(models.Model):
    class Meta:
        verbose_name_plural = 'Match Stats'
	
    # identity info
    summoner = models.ForeignKey(Summoner, on_delete=models.CASCADE)
    match = models.ForeignKey(Match, on_delete=models.CASCADE)
    champion = models.IntegerField()
    lane = models.CharField(max_length = 128)
    role = models.CharField(max_length = 128)

    # stats
    assists = models.BigIntegerField(null = True, blank = True)
    champ_level = models.BigIntegerField(null = True, blank = True)
    deaths = models.BigIntegerField(null = True, blank = True)
    double_kills = models.BigIntegerField(null = True, blank = True)
    first_blood_assist = models.NullBooleanField()
    first_blood_kill = models.NullBooleanField()
    first_inhibitor_assist = models.NullBooleanField()
    first_inhibitor_kill = models.NullBooleanField()
    first_tower_assist = models.NullBooleanField()
    first_tower_kill = models.NullBooleanField()
    gold_earned = models.BigIntegerField(null = True, blank = True)
    gold_spent = models.BigIntegerField(null = True, blank = True)
    inhibitor_kills = models.BigIntegerField(null = True, blank = True)
    item0 = models.BigIntegerField(null = True, blank = True)
    item1 = models.BigIntegerField(null = True, blank = True)
    item2 = models.BigIntegerField(null = True, blank = True)
    item3 = models.BigIntegerField(null = True, blank = True)
    item4 = models.BigIntegerField(null = True, blank = True)
    item5 = models.BigIntegerField(null = True, blank = True)
    item6 = models.BigIntegerField(null = True, blank = True)
    killing_sprees = models.BigIntegerField(null = True, blank = True)
    kills = models.BigIntegerField(null = True, blank = True)
    largest_critical_strike = models.BigIntegerField(null = True, blank = True)
    largest_killing_spree = models.BigIntegerField(null = True, blank = True)
    largest_multi_kill = models.BigIntegerField(null = True, blank = True)
    magic_damage_dealt = models.BigIntegerField(null = True, blank = True)
    magic_damage_dealt_to_champions = models.BigIntegerField(null = True, blank = True)
    magic_damage_taken = models.BigIntegerField(null = True, blank = True)
    minions_killed = models.BigIntegerField(null = True, blank = True)
    neutral_minions_killed = models.BigIntegerField(null = True, blank = True)
    neutral_minions_killed_enemy_jungle = models.BigIntegerField(null = True, blank = True)
    neutral_minions_killed_team_jungle = models.BigIntegerField(null = True, blank = True)
    penta_kills = models.BigIntegerField(null = True, blank = True)
    physical_damage_dealt = models.BigIntegerField(null = True, blank = True)
    physical_damage_dealt_to_champions = models.BigIntegerField(null = True, blank = True)
    physical_damage_taken = models.BigIntegerField(null = True, blank = True)
    quadra_kills = models.BigIntegerField(null = True, blank = True)
    sight_wards_bought_in_game = models.BigIntegerField(null = True, blank = True)
    total_damage_dealt = models.BigIntegerField(null = True, blank = True)
    total_damage_dealt_to_champions = models.BigIntegerField(null = True, blank = True)
    total_damage_taken = models.BigIntegerField(null = True, blank = True)
    total_heal = models.BigIntegerField(null = True, blank = True)
    total_time_crowd_control_dealt = models.BigIntegerField(null = True, blank = True)
    total_units_healed = models.BigIntegerField(null = True, blank = True)
    tower_kills = models.BigIntegerField(null = True, blank = True)
    triple_kills = models.BigIntegerField(null = True, blank = True)
    true_damage_dealt = models.BigIntegerField(null = True, blank = True)
    true_damage_dealt_to_champions = models.BigIntegerField(null = True, blank = True)
    true_damage_taken = models.BigIntegerField(null = True, blank = True)
    unreal_kills = models.BigIntegerField(null = True, blank = True)
    vision_wards_bought_in_game = models.BigIntegerField(null = True, blank = True)
    wards_killed = models.BigIntegerField(null = True, blank = True)
    wards_placed = models.BigIntegerField(null = True, blank = True)
    winner = models.NullBooleanField()
	
    def __str__(self):
        return self.summoner.name + "," + str(self.match.riot_id)

  


