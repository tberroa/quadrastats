import os
import pymysql
from django.core.wsgi import get_wsgi_application

pymysql.install_as_MySQLdb()
os.environ.setdefault("DJANGO_SETTINGS_MODULE", "portal.settings")
application = get_wsgi_application()
