#!/bin/bash

# set user
USER=ec2-user

# restart apache server
sudo service httpd restart

# set up configuration
sudo /home/${USER}/portal/backend/celery/prod/./update.sh

# set up permissions
sudo chmod 666 /opt/python/current/app/

# start up celery worker
sudo service celerybeat stop
sudo service celeryd stop
sudo service celeryd start
