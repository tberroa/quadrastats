![portal](resources/icon_logo.png)

# App Views
### Compare Stats to Friends and Pros
![portal](resources/app_view_row_1.png)

### Break Stats Down By Role and Champion
![portal](resources/app_view_row_2.png)

### Many Different Stats and Customizable Plotting
![portal](resources/app_view_row_3.png)

# Backend
### System Diagram
![portal](resources/system.png)

### Required Libraries
- awsebcli
- boto
- cassiopeia
- django
- pymysql
- python-memcached

### Setting Up For Linux Development
1. Make sure python is installed  
2. Install git: sudo apt-get install git  
3. Clone repo in home directory: git clone http://github.com/tberroa/portal  
4. Install pip: sudo apt-get install python-pip
5. Install python3-dev: sudo apt-get install python3-dev
6. Install virtualenv: sudo pip install virtualenv
7. Create python3 virtual environment in /backend: virtualenv -p python3 myvenv
8. Activate myvenv and pip install requirements: pip install -r requirements.txt
9. Create file /backend/portal/keys.py
10. Create file /backend/worker/keys.py
11. Create log file: sudo touch /var/log/django.log
12. Set up log file permissions: sudo chmod 666 /var/log/django.log
13. Set up AWS Elastic Beanstalk files in /backend/portal: eb init
14. Set up AWS Elastic Beanstalk files in /backend/worker: eb init

