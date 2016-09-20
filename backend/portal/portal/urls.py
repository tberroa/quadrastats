from django.conf.urls import include
from django.conf.urls import url
from django.contrib import admin
from django.contrib.auth.models import User
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from portal.keys import SUPER_USER_PASSWORD

try:
    User.objects.get(username='tberroa')
except User.DoesNotExist:
    User.objects.create_superuser(username='tberroa', email='tberroa@outlook.com', password=SUPER_USER_PASSWORD)


def loaderio(request):
    return HttpResponse('loaderio-89fb758787fb03c2be250691d3565029')


urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^loaderio-89fb758787fb03c2be250691d3565029/', csrf_exempt(loaderio)),
    url(r'^stats/', include('stats.urls')),
    url(r'^summoners/', include('summoners.urls')),
]
