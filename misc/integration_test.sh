

cd `dirname $0`
cwd=`pwd`
source util_junit.sh
source self_check.sh
source qa_check.sh

function check_arguments {
	if [ $# -lt 3 ];then
		log "usage: integration_test.sh groupId artifactId version [forkGrep]" e t
		exit 1
	fi

	if [ ! -e $TOMCAT_HOME/bin/startup.sh ];then
		log "TOMCAT_HOME does not point to a valid tomcat installation" e t
		exit 1
	else
		log "Using tomcat at $TOMCAT_HOME"
	fi

	if [ ! -e ../phoenix-kernel ];then
		echo "this script should be run with absolute path or in phoenix project directory"
		exit 1
	fi
}

function init {
	user=`whoami`
	TOMCAT_HOME=${TOMCAT_HOME:="/Users/$user/Downloads/apache-tomcat-6.0.35/"}

	groupId=$1
	artifactId=$2
	version=$3
	forkGrep=t
	if [ $# -ge 4 ];then
		forkGrep=$4
	fi
	type=war

	MAX_HTTP_TRY=30
	PHOENIX_KERNEL_WAR=../phoenix-kernel/target/phoenix-kernel.war
	PHOENIX_KERNEL_TARGET=/data/webapps/phoenix-kernel/
	PHOENIX_BOOTSTRAP_JAR=../phoenix-bootstrap/target/phoenix-bootstrap.jar
	export CATALINA_PID=/data/tomcat6.pid

	uname=`uname`
}

function retrive_war_from_maven {
	# retrive war from maven repo
	wartmp=target/wartmp/$groupId/$artifactId/$version
	rm -rf $wartmp
	mkdir -p $wartmp
	log "Retriving $groupId:$artifactId:$version:$type from maven repo" i f f
	./maven.sh $wartmp $groupId $artifactId $version $type
	mkdir $wartmp/$artifactId
	ls $wartmp/*.$type >/dev/null 2>&1
	if [ $? -eq 0 ];then
		log "Successfullt retrive $groupId:$artifactId:$version:$type from maven repo" i t
	else
		log "Failed to retrive $groupId:$artifactId:$version:$type from maven repo" e t
		exit 1
	fi
	unzip $wartmp/*.$type -d $wartmp/$artifactId >/dev/null
}

function package_phoenix {
	# package phoenix
	cd ..
	if [ uname == "Linux" ];then
		file_latest_mtime=`find -E phoenix-bootstrap phoenix-kernel -regex ".*src/main/.*|.*.xml" -type f -printf "%T@\n" | sort -n | tail -1`
	else
		file_latest_mtime=`find -E phoenix-bootstrap phoenix-kernel -regex ".*src/main/.*|.*.xml" -type f -exec stat -f "%m" {} \; | sort -n | tail -1`
	fi
	file_latest_mtime=`echo $file_latest_mtime | awk -F "." '{print $1}'`

	if [ -e phoenix-kernel/target/phoenix-kernel.war ];then
		kernel_latest_mtime=`stat -f "%m" phoenix-kernel/target/phoenix-kernel.war`
	else
		kernel_latest_mtime=1
	fi

	if [ 1$file_latest_mtime -gt 1$kernel_latest_mtime ];then
		log "Find new file in phoenix-kernel or phoenix-bootstrap, packaging phoenix..." i f f
		mvn  -Dmaven.test.skip clean package 
		if [ $? -ne 0 ];then
			log "Error package phoenix" e t
			exit 1
		fi
		log "Package done" i t
	else
		log "No new file found in phoenix-kernel or phoenix-bootstrap, skip packaging"
	fi
	cd - >/dev/null
}

function install_phoenix {
	# copy phoenix kernel, bootstrap and custom web.xml to the right place
	rm -rf $PHOENIX_KERNEL_TARGET
	mkdir -p $PHOENIX_KERNEL_TARGET
	unzip $PHOENIX_KERNEL_WAR -d $PHOENIX_KERNEL_TARGET >/dev/null
	cp -rf $PHOENIX_BOOTSTRAP_JAR $TOMCAT_HOME/lib/
}

function restart_tomcat {
	# stop all tomcat
	jps |awk '$2=="Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'

	# generate context xml file
	BIZ_WAR=$cwd/$wartmp/$artifactId
	rm -rf $TOMCAT_HOME/webapps/ROOT
	mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
	cat <<-END > $TOMCAT_HOME/conf/Catalina/localhost/ROOT.xml
	<?xml version="1.0" encoding="UTF-8"?>
	<Context docBase="$BIZ_WAR">
	   <Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader" kernelDocBase="$PHOENIX_KERNEL_TARGET" debug="true" />
	 </Context>
	END


	# clear dirs
	rm -rf $TOMCAT_HOME/logs/*

	# start tomcat
	log "Starting tomcat..." i f f
	$TOMCAT_HOME/bin/startup.sh 
	sleep 1
	tomcat_pid=`cat $CATALINA_PID`
	ps_result_row=`ps -p $tomcat_pid | wc -l`
	if [ $ps_result_row -eq 2 ];then
		log "Tomcat successfully started" i t
	else
		log "Tomcat failed to start" e t
		exit 1
	fi
}

init "$@"
check_arguments "$@"
retrive_war_from_maven "$@"
package_phoenix "$@"
install_phoenix "$@"
restart_tomcat "$@"
self_check_war "$@"
qa_check_war "$@"
