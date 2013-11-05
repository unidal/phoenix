#!/bin/bash

IP_BASE="192.168.214."
PORT=58422
USER="root"
PWD="12qwaszx"
IP_START=141
IP_END=170
CONTENT_PATTERN=`cat <<-END
{"errors":{"201":["Request params blank."]},"success":false}
END
`

for (( i=$IP_START; i<=$IP_END; i++ ))
do
    wget http://$IP_BASE$i:7463/phoenix/agent -Otest.html -q
    content=`cat test.html`
    if [ "$content" != "$CONTENT_PATTERN" ] 
    then
        echo "$IP_BASE$i does not work"
    else
        echo "$IP_BASE$i works"
    fi
    rm -rf test.html
done
