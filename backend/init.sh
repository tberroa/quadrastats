#!/bin/bash

# clean project directory
./clean.sh

# set up configuration
if [ "$1" = "--prod" ]; then
    cd celery/config/prod
    sudo ./update.sh
    sudo chmod 777 /opt/python/current/app
else 
    cd celery/config/dev
    sudo ./update.sh
fi;

# start up celery
cd ../../
./restart.sh
