from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from rest_framework.urlpatterns import format_suffix_patterns
from stats import views

urlpatterns = [
    url(r'^match/$', csrf_exempt(views.GetMatchStats.as_view())),
    url(r'^season/$', csrf_exempt(views.GetSeasonStats.as_view())),
]
urlpatterns = format_suffix_patterns(urlpatterns)
