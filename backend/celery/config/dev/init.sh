#!/bin/bash

# get user
USER=tberroa

# set up configuration
sudo /home/${USER}/portal/backend/celery/config/dev/./update.sh

# start up celery
sudo /home/${USER}/portal/backend/celery/./restart.sh
