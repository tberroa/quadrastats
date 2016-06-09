from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
  url(r'^champion/$', views.GetChampionStats.as_view()),
  url(r'^match/$', views.GetMatchStats.as_view()),
  url(r'^season/$', views.GetSeasonStats.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
