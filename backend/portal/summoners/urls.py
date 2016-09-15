from django.conf.urls import url
from django.views.decorators.csrf import csrf_exempt
from summoners import views

urlpatterns = [
    url(r'^add-friend.json', csrf_exempt(views.add_friend)),
    url(r'^change-email.json', csrf_exempt(views.change_email)),
    url(r'^change-password.json', csrf_exempt(views.change_password)),
    url(r'^get.json', csrf_exempt(views.get_summoners)),
    url(r'^login.json', csrf_exempt(views.login_user)),
    url(r'^register.json', csrf_exempt(views.register_user)),
    url(r'^remove-friend.json', csrf_exempt(views.remove_friend)),
    url(r'^reset-password.json', csrf_exempt(views.reset_password)),
    url(r'^test1.json', csrf_exempt(views.test1)),
    url(r'^test2.json', csrf_exempt(views.test2)),
]
