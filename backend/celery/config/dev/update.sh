# Edit the celery scripts and configuration files within this directory
# so that the git repo is always up to date. After making edits, run this
# script to copy the changes into the system directory where the scripts are 
# ran from.

cp celerybeat-init /etc/init.d/celerybeat
cp celerybeat-default /etc/default/celerybeat

cp celeryd-init /etc/init.d/celeryd
cp celeryd-default /etc/default/celeryd
