#!/bin/bash

# set user
USER=ec2-user

# set up configuration
sudo /home/${USER}/portal/backend/celery/prod/./update.sh

# set up permissions
sudo chmod 777 /opt/python/current/app/

# start up celery
sudo /home/${USER}/portal/backend/celery/./restart.sh

# restart apache server
#sudo service httpd restart
