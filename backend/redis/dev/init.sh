#!/bin/bash

# script for setting up the redis server on a machine used for development

# set user
USER=tberroa

# install required packages
sudo apt-get -y update
sudo apt-get -y install gcc make

# download the source files
sudo wget http://download.redis.io/releases/redis-3.2.3.tar.gz -P /usr/local/src
sudo tar xzf /usr/local/src/redis-3.2.3.tar.gz -C /usr/local/src
sudo rm -f /usr/local/src/redis-3.2.3.tar.gz

# enter the downloaded directory
cd /usr/local/src/redis-3.2.3

# compile redis
sudo make distclean
sudo make

# make directories
sudo mkdir -p /etc/redis /var/lib/redis /var/redis/6379

# setup command files
sudo cp src/redis-server src/redis-cli /usr/local/bin

# setup config file
sudo cp /home/${USER}/portal/backend/redis/dev/redis.conf /etc/redis/6379.conf

# setup init script
sudo cp /home/${USER}/portal/backend/redis/dev/redis-server /etc/init.d
sudo chmod 755 /etc/init.d/redis-server

# start up the redis server
sudo service redis-server restart




