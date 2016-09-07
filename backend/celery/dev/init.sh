#!/bin/bash

# set user
USER=tberroa

# set up configuration
sudo /home/${USER}/portal/backend/celery/dev/./update.sh

# start up celery
sudo service celerybeat stop
sudo service celeryd stop
sudo service celeryd start
sudo service celerybeat start
