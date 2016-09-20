from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from portal.riot import update

urlpatterns = [
    url(r'^update', csrf_exempt(update)),
]
