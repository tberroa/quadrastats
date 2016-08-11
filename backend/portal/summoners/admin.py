from django.contrib import admin
from summoners.models import Summoner
from summoners.models import User


class SummonerAdmin(admin.ModelAdmin):
    search_fields = ["region", "key", "name", "tier"]


class UserAdmin(admin.ModelAdmin):
    search_fields = ["email", "created"]

admin.site.register(Summoner, SummonerAdmin)
admin.site.register(User, UserAdmin)
