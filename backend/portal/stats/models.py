from django.db import models

class MatchStats(models.Model):
    class Meta:
        verbose_name_plural = 'Match Stats'
	
    # identity info
    region = models.CharField(max_length = 4, default = "")
    summoner_key = models.CharField(max_length = 32, default = "")
    summoner_name = models.CharField(max_length = 32, default = "")
    summoner_id = models.BigIntegerField(default = 0)
    match_id = models.BigIntegerField(default = 0)
    match_creation = models.BigIntegerField(default = 0)
    match_duration = models.BigIntegerField(default = 0)
    champion = models.IntegerField(default = 0)
    lane = models.CharField(max_length = 8, default = "")
    role = models.CharField(max_length = 16, default = "")
    spell1 = models.IntegerField(default = 0)
    spell2 = models.IntegerField(default = 0)
    keystone = models.BigIntegerField(null = True, blank = True)

    # raw stats
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

    # calculated stats
    cs_at_ten = models.FloatField(null = True, blank = True)
    cs_diff_at_ten = models.FloatField(null = True, blank = True)
    cs_per_min = models.FloatField(null = True, blank = True)
    dmg_per_min = models.FloatField(null = True, blank = True)
    gold_per_min = models.FloatField(null = True, blank = True)
    kda = models.FloatField(null = True, blank = True)
    kill_participation = models.FloatField(null = True, blank = True)
    team_kills = models.BigIntegerField(null = True, blank = True)
    team_deaths = models.BigIntegerField(null = True, blank = True)
    team_assists = models.BigIntegerField(null = True, blank = True)
	
    def __str__(self):
        return self.region + "," + self.summoner_name + "," + str(self.match_id)

class SeasonStats(models.Model):
    class Meta:
        verbose_name_plural = 'Season Stats'

    # identity info
    region = models.CharField(max_length = 4, default = "")
    summoner_key = models.CharField(max_length = 32, default = "")
    summoner_name = models.CharField(max_length = 32, default = "")
    summoner_id = models.BigIntegerField(default = 0)
    champion = models.IntegerField(default = 0)

    # raw stats
    kills = models.IntegerField()
    deaths = models.IntegerField()
    assists = models.IntegerField()
    doublekills = models.IntegerField()
    triplekills = models.IntegerField()
    quadrakills = models.IntegerField()
    pentakills = models.IntegerField()

  

