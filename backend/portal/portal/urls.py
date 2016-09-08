from django.conf import settings
from django.conf.urls import include
from django.conf.urls import url
from django.conf.urls.static import static
from django.contrib import admin
from django.contrib.auth.models import User
from django.http import HttpResponse
from portal.keys import SUPER_USER_PASSWORD

# create the super user account
try:
    User.objects.get(username='tberroa')
except User.DoesNotExist:
    User.objects.create_superuser(username='tberroa', email='tberroa@outlook.com', password=SUPER_USER_PASSWORD)


# setup verification file for loaderio
def loaderio(request):
    return HttpResponse('loaderio-89fb758787fb03c2be250691d3565029', content_type='text/plain')


urlpatterns = [
    url(r'^summoners/', include('summoners.urls')),
    url(r'^stats/', include('stats.urls')),
    url(r'^admin/', admin.site.urls),
    url(r'^loaderio-89fb758787fb03c2be250691d3565029/', loaderio),
]
