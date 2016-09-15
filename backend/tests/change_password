#!/bin/bash

# constants
BODY='{"region":"na","key":"frosiph","current_password":"123456","new_password":"654321"}'
FORMAT="@config/curl-format"
RESULTS_FILE=results/change_password
URL_DEV=127.0.0.1:8000/summoners/change-password.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/change-password.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Change Passwords Test"

# execute test
for i in {1..1}; do
  curl -w $FORMAT -s -X POST -d $BODY $URL_DEV >> $RESULTS_FILE &
done  

# print end of test
echo "Change Passwords Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
