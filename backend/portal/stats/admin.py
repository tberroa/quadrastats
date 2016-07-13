from django.contrib import admin

from .models import MatchStats
from .models import SeasonStats

admin.site.register(MatchStats)
admin.site.register(SeasonStats)
