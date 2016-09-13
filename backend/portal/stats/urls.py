from django.conf.urls import url
from stats import views

urlpatterns = [
    url(r'^match/$', views.GetMatchStats.as_view()),
    url(r'^season/$', views.GetSeasonStats.as_view()),
]
