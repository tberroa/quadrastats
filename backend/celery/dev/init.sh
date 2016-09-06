#!/bin/bash

# set user
USER=tberroa

# set up configuration
sudo /home/${USER}/portal/backend/celery/dev/./update.sh

# start up celery
sudo /home/${USER}/portal/backend/celery/./restart.sh
