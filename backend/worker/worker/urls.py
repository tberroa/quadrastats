from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from worker.riot import update
from worker.riot import update_all

urlpatterns = [
    url(r'^update_all', csrf_exempt(update_all)),
    url(r'^update', csrf_exempt(update)),
]
