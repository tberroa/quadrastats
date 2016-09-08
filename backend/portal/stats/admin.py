from django.contrib import admin
from stats.models import MatchStats
from stats.models import SeasonStats


class MatchStatsAdmin(admin.ModelAdmin):
    search_fields = ["region", "summoner_key", "summoner_name", "champion"]


class SeasonStatsAdmin(admin.ModelAdmin):
    search_fields = ["region", "summoner_key", "summoner_name", "champion"]


admin.site.register(MatchStats, MatchStatsAdmin)
admin.site.register(SeasonStats, SeasonStatsAdmin)
