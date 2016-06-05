from __future__ import absolute_import

import os

from celery import Celery

from datetime import timedelta

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'portal.settings')

from django.conf import settings

app = Celery('portal')

app.config_from_object('django.conf:settings')
app.autodiscover_tasks(lambda: settings.INSTALLED_APPS)

@app.task(bind=True)
def debug_task(self):
    print('Request: {0!r}'.format(self.request))

CELERYBEAT_SCHEDULE = {
    'update-all-summoners': {
        'task': 'stats.tasks.update_all',
        'schedule': timedelta(seconds=2)
    },
}
