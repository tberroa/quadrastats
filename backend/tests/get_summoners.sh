#!/bin/bash

# constants
BODY='{"region":"na","keys":["frosiph"]}'
FORMAT="@config/curl-format"
RESULTS_FILE=results/get_summoners
URL_DEV=127.0.0.1:8000/summoners/get.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/get.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Get Summoners Test"

# execute test
for i in {1..1}; do
  curl -w $FORMAT -s -X POST -d $BODY $URL_DEV >> $RESULTS_FILE &
done  

# print end of test
echo "Get Summoners Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
