#!/bin/bash

# constants
FORMAT="@config/curl-format"
INPUT_FILE=config/names_na
RESULTS_FILE=results/friends
URL_ADD_DEV=127.0.0.1:8000/summoners/add-friend.json
URL_ADD_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/add-friend.json
URL_REMOVE_DEV=127.0.0.1:8000/summoners/remove-friend.json
URL_REMOVE_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/remove-friend.json

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f $RESULTS_FILE

# print start of test
echo "Starting Add/Remove Friend Test"

# loop through list of names
while read name; do
    # construct body
    NAME=${name::-1}
    NAME=${NAME//[[:blank:]]/}
    BODY="{\"region\":\"na\",\"user_key\":\"frosiph\",\"friend_key\":\"$NAME\"}"

    # make add friend api call
    curl -w $FORMAT -s -X POST -d $BODY $URL_ADD_DEV >> $RESULTS_FILE

    # make remove friend api call
    curl -w $FORMAT -s -X POST -d $BODY $URL_REMOVE_DEV >> $RESULTS_FILE
done < $INPUT_FILE

# print end of test
echo "Add/Remove Friend Test Complete"
