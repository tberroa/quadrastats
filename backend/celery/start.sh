#!/bin/bash

sudo rm -f /var/log/celery/beat.log
sudo rm -f /var/log/celery/worker.log

sudo /etc/init.d/./celeryd start
sudo /etc/init.d/./celerybeat start
