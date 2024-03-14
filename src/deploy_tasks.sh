#!/bin/bash
[ "$(ls -A main/resources/tasks/)" ] && echo "Deploying Task Metadata" || exit 0

echo "Deploying metadata to $CONDUCTOR_SERVER_URL_$ENV using $CONDUCTOR_AUTH_KEY_$ENV and SECRET (****)"
echo "Generating the auth header using KEY and SECRET"

export response=`curl -s -X POST $CONDUCTOR_SERVER_URL_$ENV/token -H 'Content-Type:application/json' -d '{
	"keyId": "'"$CONDUCTOR_AUTH_KEY_$ENV"'",
	"keySecret": "'"$CONDUCTOR_AUTH_SECRET_$ENV"'"
}'`

if [[ "$response" != *'token'* ]]; then
  echo "Unable to generate the auth header.  Please check KEY, SECRET and CONDUCTOR_SERVER_URL variables"
  echo "Server response:"
  echo $response
  echo "environment is $ENV"
  echo "url is $CONDUCTOR_SERVER_URL_$ENV"
  exit 1
fi

export token=`echo $response | cut -d '"' -f4`

for FILE in main/resources/tasks/*;
  do
    echo "Deploying @$FILE";

    curl -X POST $CONDUCTOR_SERVER_URL_$ENV/metadata/taskdefs \
         -H "X-Authorization: $token" \
         -H "accept: */*" \
         -H "Content-Type: application/json" \
         -d @$FILE
  done






