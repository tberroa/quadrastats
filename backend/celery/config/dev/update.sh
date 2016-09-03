#!/bin/bash

# Edit the celery scripts and configuration files within this directory
# so that the git repo is always up to date. After making edits, run this
# script to copy the changes into the system directory where the scripts are 
# ran from.

# get user
USER=tberroa

# update files
cp /home/${USER}/portal/backend/celery/config/dev/celerybeat-init /etc/init.d/celerybeat
cp /home/${USER}/portal/backend/celery/config/dev/celerybeat-default /etc/default/celerybeat
cp /home/${USER}/portal/backend/celery/config/dev/celeryd-init /etc/init.d/celeryd
cp /home/${USER}/portal/backend/celery/config/dev/celeryd-default /etc/default/celeryd
