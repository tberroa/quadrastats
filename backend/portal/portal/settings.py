import os
from datetime import timedelta
from kombu import Queue
from portal.keys import DJANGO_SECRET_KEY
from portal.keys import EMAIL_PASSWORD
from portal.keys import RDS_PASSWORD

# Django Settings
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SECRET_KEY = DJANGO_SECRET_KEY
DEBUG = False
ALLOWED_HOSTS = ['*']
INSTALLED_APPS = [
    'summoners',
    'stats',
    'rest_framework',
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
]
MIDDLEWARE_CLASSES = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]
ROOT_URLCONF = 'portal.urls'
TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]
WSGI_APPLICATION = 'portal.wsgi.application'
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
AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]
PASSWORD_HASHERS = [
    'django.contrib.auth.hashers.PBKDF2PasswordHasher',
    'django.contrib.auth.hashers.PBKDF2SHA1PasswordHasher',
    'django.contrib.auth.hashers.BCryptSHA256PasswordHasher',
    'django.contrib.auth.hashers.BCryptPasswordHasher',
    'django.contrib.auth.hashers.SHA1PasswordHasher',
    'django.contrib.auth.hashers.MD5PasswordHasher',
    'django.contrib.auth.hashers.CryptPasswordHasher',
]
LANGUAGE_CODE = 'en-us'
TIME_ZONE = 'UTC'
USE_I18N = True
USE_L10N = True
USE_TZ = False
STATIC_URL = '/static/'
STATIC_ROOT = BASE_DIR + '/static/'
STATICFILES_FINDERS = [
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
]
REST_FRAMEWORK = {
    'DEFAULT_THROTTLE_CLASSES': (
        'rest_framework.throttling.AnonRateThrottle',
        'rest_framework.throttling.UserRateThrottle'
    ),
    'DEFAULT_THROTTLE_RATES': {
        'anon': '500000/sec',
        'user': '500000/sec'
    }
}

# Celery Settings
BROKER_URL = 'redis://localhost:6379/0'
BROKER_TRANSPORT_OPTIONS = {
    'fanout_prefix': True,
    'fanout_patterns': True,
}
CELERY_RESULT_BACKEND = 'redis://localhost:6379/0'
CELERY_ACCEPT_CONTENT = ['pickle']
CELERY_TASK_SERIALIZER = 'pickle'
CELERY_RESULT_SERIALIZER = 'pickle'
CELERY_DEFAULT_QUEUE = 'celery'
CELERY_QUEUES = (
    Queue('celery', routing_key='celery'),
    Queue('celery_br', routing_key='celery_br'),
    Queue('celery_eune', routing_key='celery_eune'),
    Queue('celery_euw', routing_key='celery_euw'),
    Queue('celery_jp', routing_key='celery_jp'),
    Queue('celery_kr', routing_key='celery_kr'),
    Queue('celery_lan', routing_key='celery_lan'),
    Queue('celery_las', routing_key='celery_las'),
    Queue('celery_na', routing_key='celery_na'),
    Queue('celery_oce', routing_key='celery_oce'),
    Queue('celery_ru', routing_key='celery_ru'),
    Queue('celery_tr', routing_key='celery_tr'),
)
CELERYBEAT_SCHEDULE = {
    'update_br': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["br"],
        'options': {'queue': 'celery_br'}
    },
    'update_eune': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["eune"],
        'options': {'queue': 'celery_eune'}
    },
    'update_euw': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["euw"],
        'options': {'queue': 'celery_euw'}
    },
    'update_jp': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["jp"],
        'options': {'queue': 'celery_jp'}
    },
    'update_kr': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["kr"],
        'options': {'queue': 'celery_kr'}
    },
    'update_lan': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["lan"],
        'options': {'queue': 'celery_lan'}
    },
    'update_las': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["las"],
        'options': {'queue': 'celery_las'}
    },
    'update_na': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["na"],
        'options': {'queue': 'celery_na'}
    },
    'update_oce': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["oce"],
        'options': {'queue': 'celery_oce'}
    },
    'update_ru': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["ru"],
        'options': {'queue': 'celery_ru'}
    },
    'update_tr': {
        'task': 'portal.tasks.update',
        'schedule': timedelta(minutes=20),
        'args': ["tr"],
        'options': {'queue': 'celery_tr'}
    },
}

# Email Settings
DEFAULT_FROM_EMAIL = 'tberroa@outlook.com'
EMAIL_USE_TLS = True
EMAIL_HOST = 'smtp-mail.outlook.com'
EMAIL_HOST_USER = 'tberroa@outlook.com'
EMAIL_HOST_PASSWORD = EMAIL_PASSWORD
EMAIL_PORT = 587
