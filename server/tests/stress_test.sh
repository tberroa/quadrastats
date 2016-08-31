#!/bin/bash

# remove old results files
rm -f results/st_1.txt
rm -f results/st_2.txt

# constants
BODY='{"region":"na","key":"frosiph","password":"123456"}'
FORMAT="@../config/curl-format.txt"
START=$(date +%s)
URL=54.196.223.185/summoners/login/

# execute test
for i in {1..500}; do
  curl -w $FORMAT -o /dev/null -s -H "Content-Type: application/json" -X POST -d $BODY $URL >> results/st_1.txt &
  END=$(date +%s)
  DIFF=$(($END - $START))
  echo "It took $DIFF seconds" >> results/st_2.txt
done  

# command for checking number of curl processes: ps aux | grep curl | wc -l
