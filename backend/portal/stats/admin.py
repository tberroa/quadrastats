from django.contrib import admin

from stats.models import SeasonStats
from stats.models import ChampionStats
from stats.models import Match
from stats.models import MatchStats

admin.site.register(SeasonStats)
admin.site.register(ChampionStats)
admin.site.register(Match)
admin.site.register(MatchStats)
