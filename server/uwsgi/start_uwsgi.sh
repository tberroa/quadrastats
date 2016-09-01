#!/bin/bash

# reset log file
rm -f /home/ubuntu/logs/uwsgi/uwsgi.log

# activate virtual environment
source /home/ubuntu/portal/backend/myvenv/bin/activate

# make sure somaxconn concurrency is set to proper value
sudo sysctl -w net.core.somaxconn=4096

# execute uwsgi ini file
uwsgi --ini /home/ubuntu/portal/server/config/uwsgi.ini
