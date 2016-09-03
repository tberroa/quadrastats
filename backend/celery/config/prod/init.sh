#!/bin/bash

# get user
USER=ec2-user

# set up configuration
sudo /home/${USER}/portal/backend/celery/config/prod/./update.sh

# start up celery
sudo /home/${USER}/portal/backend/celery/./restart.sh
