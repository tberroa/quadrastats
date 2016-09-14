#!/bin/bash

# ensure results directory exists
mkdir -p results

# remove old results files
rm -f results/login

# constants
BODY='{"region":"na","key":"frosiph","password":"123456"}'
FORMAT="@config/curl-format"
URL_DEV=127.0.0.1:8000/summoners/login.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/login.json

# print start of test
echo "Starting Login Test"

# execute test
for i in {1..10}; do
  curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL_PROD >> results/login &
done  

# print end of test
echo "Login Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
