#!/bin/bash

# constants
BODY='{"region":"na","key":"frosiph"}'
FORMAT="@config/curl-format"
RESULTS_FILE=results/update
URL_DEV=127.0.0.1:8000/update.json
URL_PROD=portal-domain.us-east-1.elasticbeanstalk.com/update.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Update Test"

# execute test
for i in {1..1}; do
  curl -w $FORMAT -s -X POST -d $BODY $URL_DEV >> $RESULTS_FILE &
done  

# print end of test
echo "Update Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
