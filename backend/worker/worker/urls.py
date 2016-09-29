from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from worker.riot import update
from worker.riot import update_active
from worker.riot import update_nonactive

urlpatterns = [
    url(r'^update_active', csrf_exempt(update_active)),
    url(r'^update_nonactive', csrf_exempt(update_nonactive)),
    url(r'^update', csrf_exempt(update)),
]
