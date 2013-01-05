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

ensure_not_empty container_install_path="$container_install_path" container_type="$container_type" op="$op"

############################## functions for dev ############################## 
function kill_jboss {
	service web stop
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
	service web start
	log "jboss started"
}

function container_status_inner {
	# remove last /
	last_alphabet_idx=$((${#container_install_path}-1))
	if [ ${container_install_path:$last_alphabet_idx} == "/" ];then
		container_install_path=${container_install_path:0:$last_alphabet_idx}
	fi

	container_up=1
	case $container_type in
			tomcat)		pid=`jps -lvm | awk -v tocheck=$container_install_path '$2=="org.apache.catalina.startup.Bootstrap" && index($0, tocheck)>0{pid=$1;print pid;}'`;;
			jboss)		pid=`jps -lvm | awk -v tocheck=$container_install_path '$2=="org.jboss.Main" && index($0, tocheck)>0{pid=$1;print pid;}'`;;
	esac

	if [ x$pid != x ];then
		container_up=0
	fi

	exit $container_up
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

function container_status {
	container_status_inner
}

$op
