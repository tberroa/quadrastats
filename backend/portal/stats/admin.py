from django.contrib import admin

from .models import MatchStats
from .models import SeasonStats
from .models import SeasonStatsChampion

admin.site.register(MatchStats)
admin.site.register(SeasonStats)
admin.site.register(SeasonStatsChampion)
