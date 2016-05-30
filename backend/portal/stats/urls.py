from django.conf.urls import url

from rest_framework.urlpatterns import format_suffix_patterns

from stats import views

urlpatterns = [
  url(r'^recent/get/$', views.GetRecentStats.as_view()),
  url(r'^season/get/$', views.GetSeasonStats.as_view()),
  url(r'^champion/get/$', views.GetChampionStats.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
