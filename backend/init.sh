#!/bin/bash

# clean project directory
./clean.sh

# move celery config files to proper location
if [ "$1" = "-dev" ]; then
    cd celery/config/dev/
    sudo ./update.sh
else 
    cd celery/config/prod
    sudo ./update.sh
fi;

# start up celery
cd ../../
./restart.sh
