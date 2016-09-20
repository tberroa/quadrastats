import os
from worker.keys import DJANGO_SECRET_KEY
from worker.keys import RDS_PASSWORD

# django settings
ALLOWED_HOSTS = ['*']
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CACHES = {
    'default': {
        'BACKEND': 'django.core.cache.backends.memcached.MemcachedCache',
        'LOCATION': 'portal.vf9jgc.cfg.use1.cache.amazonaws.com:11211',
     }
}
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'portaldb',
        'USER': 'tberroa',
        'PASSWORD': RDS_PASSWORD,
        'HOST': 'portaldb.cflq9mp1c8f1.us-east-1.rds.amazonaws.com',
        'PORT': '3306',
    }
}
DEBUG = False
INSTALLED_APPS = [
    'summoners',
    'stats',
]
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'formatters': {
        'standard': {
            'format': '%(asctime)s [%(levelname)s] %(name)s: %(message)s'
        },
    },
    'handlers': {
        'default': {
            'level':'DEBUG',
            'class':'logging.handlers.RotatingFileHandler',
            'filename': '/var/log/django.log',
            'maxBytes': 5242880,
            'formatter':'standard',
        },  
    },
    'loggers': {
        'django': {
            'handlers': ['default'],
            'level': 'DEBUG',
            'propagate': True
        },
    },
}
MIDDLEWARE_CLASSES = []
PASSWORD_HASHERS = []
ROOT_URLCONF = 'worker.urls'
TIME_ZONE = 'UTC'
USE_I18N = False
SECRET_KEY = DJANGO_SECRET_KEY
WSGI_APPLICATION = 'worker.wsgi.application'
