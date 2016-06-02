from django.contrib import admin

from summoners.models import User
from summoners.models import Summoner

admin.site.register(User)
admin.site.register(Summoner)
