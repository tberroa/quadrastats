from django.conf.urls import url
from summoners import views

urlpatterns = [
    url(r'^add-friend/$', views.add_friend),
    url(r'^change-email/$', views.change_email),
    url(r'^change-password/$', views.change_password),
    url(r'^get/$', views.get_summoners),
    url(r'^login/$', views.login_user),
    url(r'^register/$', views.register_user),
    url(r'^remove-friend/$', views.remove_friend),
    url(r'^reset-password/$', views.reset_password),
    url(r'^test1/$', views.test1),
]