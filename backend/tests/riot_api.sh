#!/bin/bash

# ensure results directory exists
mkdir -p results

# remove old results files
rm -f results/riot_api

# constants
BODY='{"region":"na","key":"frosiph"}'
FORMAT="@config/curl-format"
URL_DEV=127.0.0.1:8000/summoners/test1.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/test1.json

# print start of test
echo "Starting Riot API Test"

# execute test
for i in {1..10}; do
  curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL_PROD >> results/riot_api &
done  

# print end of test
echo "Riot API Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
