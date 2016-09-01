#!/bin/bash

# reset log file
sudo rm -f /var/log/nginx/error.log

# restart nginx
sudo service nginx restart
