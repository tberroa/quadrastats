from django.contrib import admin

from .models import ChampionStats
from .models import MatchStats
from .models import SeasonStats

admin.site.register(ChampionStats)
admin.site.register(MatchStats)
admin.site.register(SeasonStats)
