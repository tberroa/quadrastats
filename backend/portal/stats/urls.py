from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from stats import views

urlpatterns = [
    url(r'^match.json', csrf_exempt(views.get_match_stats)),
    url(r'^season.json', csrf_exempt(views.get_season_stats)),
]
