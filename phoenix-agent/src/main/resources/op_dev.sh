#!/bin/bash

set -e
set -u

cd `dirname $0`
source util.sh

log "PID is $$"
log "CMD is $0 $@"

while getopts ":o:b:c:" option;do
	case $option in
			o)      op=$OPTARG;;
			b)      container_install_path=$OPTARG;;
			c)		container_type=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
	esac
done

ensure_not_empty container_install_path="$container_install_path" container_type="$container_type"

############################## functions for dev ############################## 
function kill_jboss {
	jps -lvm | awk '$2=="org.jboss.Main"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function kill_tomcat {
	jps -lvm | awk '$2=="org.apache.catalina.startup.Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function start_tomcat {
	log "starting tomcat"
	$container_install_path/bin/startup.sh
	log "tomcat started"
}

function start_jboss {
	log "starting jboss"
	$container_install_path/bin/run.sh >/dev/null 2>&1 &
	log "jboss started"
}

############################## interface with agent.sh ############################## 

# stop container and make offline in dpsf and f5
function stop_all {
	log "stopping container $container_type"
	kill_jboss
	kill_tomcat
	log "container stopped"
}

# start container only
function start_container {
	if [ $container_type ==	"tomcat" ];then
		start_tomcat
	else
		start_jboss
	fi
}

# make container online in dpsf and f5
function put_container_online {
	log "dpsf online"
	log "f5nodeonline"
}

$op
