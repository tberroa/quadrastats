#!/bin/bash

# constants
BODY='{"region":"na","key":"frosiph","password":"123456"}'
FORMAT="@config/curl-format"
RESULTS_FILE=results/login
URL_DEV=127.0.0.1:8000/summoners/login.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/login.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Login Test"

# execute test
for i in {1..1}; do
  curl -w $FORMAT -s -X POST -d $BODY $URL_DEV >> $RESULTS_FILE &
done  

# print end of test
echo "Login Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
