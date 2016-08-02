from django.contrib import admin
from stats.models import MatchStats
from stats.models import SeasonStats

admin.site.register(MatchStats)
admin.site.register(SeasonStats)
