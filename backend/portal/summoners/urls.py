from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from rest_framework.urlpatterns import format_suffix_patterns
from summoners import views

urlpatterns = [
    url(r'^add-friend/$', csrf_exempt(views.AddFriend.as_view())),
    url(r'^change-email/$', csrf_exempt(views.ChangeEmail.as_view())),
    url(r'^change-password/$', csrf_exempt(views.ChangePassword.as_view())),
    url(r'^get/$', csrf_exempt(views.GetSummoners.as_view())),
    url(r'^login/$', csrf_exempt(views.LoginUser.as_view())),
    url(r'^register/$', csrf_exempt(views.RegisterUser.as_view())),
    url(r'^remove-friend/$', csrf_exempt(views.RemoveFriend.as_view())),
    url(r'^reset-password/$', csrf_exempt(views.ResetPassword.as_view())),
    url(r'^test1/$', csrf_exempt(views.Test1.as_view())),
]
urlpatterns = format_suffix_patterns(urlpatterns)
