#!/bin/bash

# Edit the celery scripts and configuration files within this directory
# so that the git repo is always up to date. After making edits, run this
# script to copy the changes into the system directory where the scripts are 
# ran from.

# get user
USER=ec2-user

# update files
sudo cp /home/${USER}/portal/backend/celery/prod/celerybeat-init /etc/init.d/celerybeat
sudo cp /home/${USER}/portal/backend/celery/prod/celerybeat-default /etc/default/celerybeat
sudo cp /home/${USER}/portal/backend/celery/prod/celeryd-init /etc/init.d/celeryd
sudo cp /home/${USER}/portal/backend/celery/prod/celeryd-default /etc/default/celeryd
