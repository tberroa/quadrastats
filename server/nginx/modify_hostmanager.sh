#!/bin/bash

cd /opt/elasticbeanstalk/srv/hostmanager/lib/elasticbeanstalk/hostmanager
cp utils/apacheutil.rb utils/nginxutil.rb
sed -i 's/Apache/Nginx/g' utils/nginxutil.rb
sed -i 's/apache/nginx/g' utils/nginxutil.rb
sed -i 's/httpd/nginx/g' utils/nginxutil.rb
cp init-tomcat.rb init-tomcat.rb.orig
sed -i 's/Apache/Nginx/g' init-tomcat.rb
sed -i 's/apache/nginx/g' init-tomcat.rb
