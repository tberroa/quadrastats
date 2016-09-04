# Portal
League of Legends app for easily comparing in-game stats and player performance.


SETTING UP FOR DEVELOPMENT - Linux
- Make sure python is installed
- Install git: sudo apt-get install git
- Clone repo in home directory: git clone http://github.com/tberroa/portal
- Install pip: sudo apt-get install python-pip
- Install python3-dev: sudo apt-get install python3-dev
- Install virtualenv: sudo pip install virtualenv
- Create python3 virtual environment in /backend: virtualenv -p python3 myvenv
- Activate myvenv and pip install requirements: pip install -r requirements.txt
- Create backend/portal/keys.py
- Update database: python manage.py migrate
- Set up AWS Elastic Beanstalk files in /backend/portal: eb init

