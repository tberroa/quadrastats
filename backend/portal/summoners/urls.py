from django.conf.urls import url

from rest_framework.urlpatterns import format_suffix_patterns

from summoners import views

urlpatterns = [
  url(r'^register/$', views.RegisterUser.as_view()),
  url(r'^login/$', views.LoginUser.as_view()),
  url(r'^get/$', views.GetSummoners.as_view()),
  url(r'^create/$', views.CreateSummoner.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
