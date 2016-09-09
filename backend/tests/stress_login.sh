#!/bin/bash

# remove old results files
rm -f results/login_1
rm -f results/login_2

# constants
BODY='{"region":"na","key":"frosiph","password":"123456"}'
FORMAT="@config/curl-format"
START=$(date +%s)
URL=portal-domain.us-east-1.elasticbeanstalk.com/summoners/login/

# print start of test
echo "Starting Login Stress Test"

# execute test
for i in {1..10}; do
  curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL >> results/login_1 &
  END=$(date +%s)
  DIFF=$(($END - $START))
  echo "It took $DIFF seconds" >> results/login_2
done  

# print end of test
echo "Login Stress Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
