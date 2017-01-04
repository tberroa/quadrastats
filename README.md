![portal](resources/icon_logo.png)

# App Preview
### Compare Stats to Friends and Pros
![portal](resources/b1_friends.png)
![portal](resources/b1_recent.png)
![portal](resources/b1_season.png)
![portal](resources/b1_with_friends.png)

### Break Stats Down By Role and Champion
![portal](resources/b2_filter.png)
![portal](resources/b2_recent_filtered.png)
![portal](resources/b2_season_filtered.png)
![portal](resources/b2_win_rates_filtered.png)

### Many Different Stats and Customizable Plotting
![portal](resources/b3_first_tower.png)
![portal](resources/b3_kills.png)
![portal](resources/b3_plot_summoners.png)
![portal](resources/b3_wards_bought.png)

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

