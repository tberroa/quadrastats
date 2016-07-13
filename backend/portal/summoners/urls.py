from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
  url(r'^add-friend/$', views.AddFriend.as_view()),
  url(r'^change-email/$', views.ChangeEmail.as_view()),
  url(r'^change-password/$', views.ChangePassword.as_view()),
  url(r'^get/$', views.GetSummoners.as_view()),
  url(r'^login/$', views.LoginUser.as_view()),
  url(r'^register/$', views.RegisterUser.as_view()),
  url(r'^remove-friend/$', views.RemoveFriend.as_view()),
  url(r'^reset-password/$', views.ResetPassword.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
