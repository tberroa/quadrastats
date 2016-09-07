from __future__ import absolute_import

import os
import pymysql
from celery import Celery
from django.conf import settings

pymysql.install_as_MySQLdb()

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'portal.settings')

app = Celery('portal')

app.config_from_object('django.conf:settings')

app.autodiscover_tasks(lambda: settings.INSTALLED_APPS)


@app.task(bind=True)
def debug_task(self):
    print('Request: {0!r}'.format(self.request))
