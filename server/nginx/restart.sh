#!/bin/bash

# reset log files
sudo rm -f /var/log/httpd/elasticbeanstalk-access_log
sudo rm -f /var/log/httpd/elasticbeanstalk-error_log

# restart nginx
sudo service nginx restart
