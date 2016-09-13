import os
from datetime import timedelta
from portal.keys import DJANGO_SECRET_KEY
from portal.keys import EMAIL_PASSWORD
from portal.keys import RDS_PASSWORD

# django settings
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
CACHES = {
    'default': {
        'BACKEND': 'django_redis.cache.RedisCache',
        'LOCATION': 'redis://127.0.0.1:6379/1',
        'OPTIONS': {
            'CLIENT_CLASS': 'django_redis.client.DefaultClient',
        }
    }
}

# celery settings
BROKER_URL = 'redis://localhost:6379/0'
BROKER_TRANSPORT_OPTIONS = {
    'fanout_prefix': True,
    'fanout_patterns': True,
}
CELERY_ACCEPT_CONTENT = ['pickle']
CELERY_TASK_SERIALIZER = 'pickle'
CELERY_RESULT_SERIALIZER = 'pickle'
CELERYBEAT_SCHEDULE = {
    'update_br': {
        'task': 'portal.tasks.update_br',
        'schedule': timedelta(minutes=20)
    },
    'update_eune': {
        'task': 'portal.tasks.update_eune',
        'schedule': timedelta(minutes=20)
    },
    'update_euw': {
        'task': 'portal.tasks.update_euw',
        'schedule': timedelta(minutes=20)
    },
    'update_jp': {
        'task': 'portal.tasks.update_jp',
        'schedule': timedelta(minutes=20)
    },
    'update_kr': {
        'task': 'portal.tasks.update_kr',
        'schedule': timedelta(minutes=20)
    },
    'update_lan': {
        'task': 'portal.tasks.update_lan',
        'schedule': timedelta(minutes=20)
    },
    'update_las': {
        'task': 'portal.tasks.update_las',
        'schedule': timedelta(minutes=20)
    },
    'update_na': {
        'task': 'portal.tasks.update_na',
        'schedule': timedelta(minutes=20)
    },
    'update_oce': {
        'task': 'portal.tasks.update_oce',
        'schedule': timedelta(minutes=20)
    },
    'update_ru': {
        'task': 'portal.tasks.update_ru',
        'schedule': timedelta(minutes=20)
    },
    'update_tr': {
        'task': 'portal.tasks.update_tr',
        'schedule': timedelta(minutes=20)
    },
}

# email settings
DEFAULT_FROM_EMAIL = 'tberroa@outlook.com'
EMAIL_USE_TLS = True
EMAIL_HOST = 'smtp-mail.outlook.com'
EMAIL_HOST_USER = 'tberroa@outlook.com'
EMAIL_HOST_PASSWORD = EMAIL_PASSWORD
EMAIL_PORT = 587
