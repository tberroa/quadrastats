from django.contrib import admin

from .models import ChampionStats
from .models import Match
from .models import MatchStats
from .models import SeasonStats

admin.site.register(SeasonStats)
admin.site.register(ChampionStats)
admin.site.register(Match)
admin.site.register(MatchStats)
