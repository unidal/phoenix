#!/bin/bash

IP_BASE="192.168.214."
PORT=58422
USER="root"
PWD="12qwaszx"
IP_START=141
IP_END=170

if [ $# != 1 ] ; then 
    echo "USAGE: $0 war_path" 
    echo " e.g.: $0 http://192.168.22.158:8000/phoenix-dev-agent.war " 
    exit 1; 
fi 

for (( i=$IP_START; i<=$IP_END; i++ ))
do
    ./deploy.sh $IP_BASE$i $PORT $USER $PWD $1
done
