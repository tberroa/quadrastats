#!/bin/bash

# ensure results directory exists
mkdir -p results

# remove old results files
rm -f results/register

# constants
BODY='{"region":"na","key":"frosiph","email":"tberroa@outlook.com","password":"123456","code":"AD"}'
FORMAT="@config/curl-format"
URL=portal-domain.us-east-1.elasticbeanstalk.com/summoners/register.json

# print start of test
echo "Starting Register Test"

# execute test
for i in {1..10}; do
  curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL >> results/register &
done  

# print end of test
echo "Register Test Complete"

# command for checking number of curl processes: ps aux | grep curl | wc -l
