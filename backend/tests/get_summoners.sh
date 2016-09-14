#!/bin/bash

# ensure results directory exists
mkdir -p results

# remove old results files
rm -f results/get_summoners

# constants
BODY='{"region":"na","keys":["frosiph", "fans"]}'
FORMAT="@config/curl-format"
URL_DEV=127.0.0.1:8000/summoners/get.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/get.json

# print start of test
echo "Starting Get Summoners Test"

# execute test
for i in {1..10}; do
  curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL_DEV >> results/get_summoners &
done  

# print end of test
echo "Get Summoners Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
