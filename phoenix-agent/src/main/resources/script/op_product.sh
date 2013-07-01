#!/bin/bash

set -e
set -u

cd `dirname $0`
source ./util.sh

log "PID is $$"
log "CMD is $0 $@"

while getopts ":o:b:c:" option;do
	case $option in
			o)      op=$OPTARG;;
			b)      container_install_path=$OPTARG;;
			c)		container_type=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
	esac
done

OP_SCRIPT=/etc/init.d/$container_type

# stop container and make offline in dpsf and f5
function stop_all {
	sudo -u root $OP_SCRIPT stop || { log_error "fail to stop container and make it offline, exit code is $?"; exit 1; }
}

# start container only
function start_container {
	sudo -u root $OP_SCRIPT forcestart || { log_error "fail to start container, exit code is $?"; exit 1; }
}

# make container online in dpsf and f5
function put_container_online {
	sudo -u root $OP_SCRIPT dpsfonline || { log_error "fail to make container dpsf online, exit code is $?"; exit 1; }
	sudo -u root $OP_SCRIPT f5online || { log_error "fail to make container f5 online, exit code is $?"; exit 1; }
}

function container_status {
	sudo -u root $OP_SCRIPT status || { exit 1; }
}

$op
