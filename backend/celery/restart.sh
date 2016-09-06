#!/bin/bash

sudo service celerybeat stop
sudo service celeryd stop 

sudo rm -f /var/log/celery/beat.log
sudo rm -f /var/log/celery/worker.log

sudo service celeryd start
sudo service celerybeat start
