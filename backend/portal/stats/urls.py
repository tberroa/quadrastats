from django.conf.urls import url
from stats import views

urlpatterns = [
    url(r'^match.json', views.get_match_stats),
    url(r'^season.json', views.get_season_stats),
]
