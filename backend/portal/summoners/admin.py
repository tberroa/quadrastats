from django.contrib import admin

from .models import Summoner
from .models import User

admin.site.register(Summoner)
admin.site.register(User)
