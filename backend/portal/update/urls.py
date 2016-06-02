from django.conf.urls import url

from rest_framework.urlpatterns import format_suffix_patterns

from update import views

urlpatterns = [
  url(r'^command/$', views.Command.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
