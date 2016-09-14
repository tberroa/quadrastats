#!/bin/bash

# ensure results directory exists
mkdir -p results

# remove old results file
rm -f results/friends

# constants
FORMAT="@config/curl-format"
URL_ADD_DEV=127.0.0.1:8000/summoners/add-friend.json
URL_REMOVE_DEV=127.0.0.1:8000/summoners/remove-friend.json
URL_ADD_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/add-friend.json
URL_REMOVE_PROD=portal-domain.us-east-1.elasticbeanstalk.com/summoners/remove-friend.json

# print start of test
echo "Starting Add/Remove Friend Test"

# loop through list of names
while read name; do
    # construct body
    NAME=${name::-1}
    NAME=${NAME//[[:blank:]]/}
    BODY="{\"region\":\"na\",\"user_key\":\"frosiph\",\"friend_key\":\"$NAME\"}"

    # make add friend api call
    curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL_ADD_PROD >> results/friends

    # make remove friend api call
    curl -w $FORMAT -s -H "Content-Type: application/json" -X POST -d $BODY $URL_REMOVE_PROD >> results/friends
done <config/names_na

# print end of test
echo "Add/Remove Friend Test Complete"
