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
    summoner = models.ForeignKey(Summoner, on_delete=models.CASCADE)
    match = models.ForeignKey(Match, on_delete=models.CASCADE)
    champion = models.IntegerField()
    lane = models.CharField(max_length = 128)
    role = models.CharField(max_length = 128)
    kills = models.BigIntegerField(null = True, blank = True)
    deaths = models.BigIntegerField(null = True, blank = True)
    assists = models.BigIntegerField(null = True, blank = True)

    def __str__(self):
        return self.summoner.name + "," + str(self.match.riot_id)

  


