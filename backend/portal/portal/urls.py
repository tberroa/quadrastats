from django.conf import settings
from django.conf.urls import include
from django.conf.urls import url
from django.conf.urls.static import static
from django.contrib import admin
from django.http import HttpResponse


def loaderio(request):
    return HttpResponse('loaderio-89fb758787fb03c2be250691d3565029', content_type='text/plain')

urlpatterns = [
    url(r'^loaderio-89fb758787fb03c2be250691d3565029/', loaderio),
    url(r'^summoners/', include('summoners.urls')),
    url(r'^stats/', include('stats.urls')),
    url(r'^admin/', admin.site.urls),
]
