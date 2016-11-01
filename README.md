# Portal
League of Legends app for easily comparing in-game stats and player performance.

- Check out app at http://quadrastats.com

## Backend
**Required Libraries**
- awsebcli
- boto
- cassiopeia
- django
- pymysql
- python-memcached

**Setting Up For Linux Development**
- Make sure python is installed
- Install git: sudo apt-get install git
- Clone repo in home directory: git clone http://github.com/tberroa/portal
- Install pip: sudo apt-get install python-pip
- Install python3-dev: sudo apt-get install python3-dev
- Install virtualenv: sudo pip install virtualenv
- Create python3 virtual environment in /backend: virtualenv -p python3 myvenv
- Activate myvenv and pip install requirements: pip install -r requirements.txt
- Create file /backend/portal/keys.py
- Create file /backend/worker/keys.py
- Create log file: sudo touch /var/log/django.log
- Set up log file permissions: sudo chmod 666 /var/log/django.log
- Set up AWS Elastic Beanstalk files in /backend/portal: eb init
- Set up AWS Elastic Beanstalk files in /backend/worker: eb init
