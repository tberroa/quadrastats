from django.contrib import admin

from .models import User
from .models import Summoner

admin.site.register(User)
admin.site.register(Summoner)
