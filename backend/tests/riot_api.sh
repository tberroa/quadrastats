#!/bin/bash

# constants
BODY='{"region":"na","key":"frosiph"}'
FORMAT="@config/curl-format"
RESULTS_FILE=results/riot_api
URL_DEV=127.0.0.1:8000/summoners/test1.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/test1.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Riot API Test"

# execute test
for i in {1..1}; do
  curl -w $FORMAT -s -X POST -d $BODY $URL_DEV >> $RESULTS_FILE &
done  

# print end of test
echo "Riot API Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
