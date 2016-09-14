from django.conf.urls import url
from summoners import views

urlpatterns = [
    url(r'^add-friend.json', views.add_friend),
    url(r'^change-email.json', views.change_email),
    url(r'^change-password.json', views.change_password),
    url(r'^get.json', views.get_summoners),
    url(r'^login.json', views.login_user),
    url(r'^register.json', views.register_user),
    url(r'^remove-friend.json', views.remove_friend),
    url(r'^reset-password.json', views.reset_password),
    url(r'^test1.json', views.test1),
]
