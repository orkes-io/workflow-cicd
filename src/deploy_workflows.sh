#!/bin/bash
[ "$(ls -A main/resources/workflows/)" ] && echo "Deploying Workflow Metadata" || exit 0

echo "Deploying metadata to $CONDUCTOR_SERVER_URL using $KEY and SECRET (****)"
echo "Generating the auth header using KEY and SECRET"

# docs-marker-start-1
export response=`curl -s -X POST $CONDUCTOR_SERVER_URL/token -H 'Content-Type:application/json' -d '{
	"keyId": "'"$CONDUCTOR_AUTH_KEY"'",
	"keySecret": "'"$CONDUCTOR_AUTH_SECRET"'"
}'`

if [[ "$response" != *'token'* ]]; then
  echo "Unable to generate the auth header.  Please check KEY, SECRET and CONDUCTOR_SERVER_URL variables"
  echo "Server response:"
  echo $response
  exit 1
fi

export token=`echo $response | cut -d '"' -f4`

for FILE in main/resources/workflows/*;
  do
    echo "Deploying @$FILE";

    curl -X POST $CONDUCTOR_SERVER_URL/metadata/workflow?overwrite=true \
         -H "X-Authorization: $token" \
         -H "accept: */*" \
         -H "Content-Type: application/json" \
         -d @$FILE
  done

# docs-marker-end-1




