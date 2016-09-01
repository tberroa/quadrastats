#!/bin/bash

sudo cp /home/$USER/portal/server/nginx/nginx.conf /etc/nginx/nginx.conf
sudo cp /home/$USER/portal/server/nginx/beanstalk.conf /etc/nginx/conf.d/beanstalk.conf
sudo cp /home/$USER/portal/server/nginx/proxy.conf /etc/nginx/conf.d/proxy.conf
