


cd `dirname $0`
cwd=`pwd`
source util_junit.sh
source self_check.sh
source qa_check.sh
user=`whoami`
uname=`uname`

TOMCAT_HOME=${TOMCAT_HOME:="/Users/$user/Downloads/apache-tomcat-6.0.35/"}
JBOSS_HOME=${JBOSS_HOME:="/Users/$user/Downloads/jboss-4.2.2.GA/"}

function check_arguments {
	container=jboss
	while getopts "g:a:v:c:w:" option;do
		case $option in
                g)      groupId=$OPTARG;;
                a)      artifactId=$OPTARG;;
                v)      version=$OPTARG;;
                c)      container=$OPTARG;;
				w)		war=$OPTARG;;
                \?)     usage;;
        esac
	done
	forkGrep=f
	type=war

	if [ x$war == x ];then
		if [[ x$groupId == x || x$artifactId == x || x$version == x ]];then
			log "usage: `basename $0`  -g groupId -a artifactId -v version [-c container]" e t
			exit 1
		fi
		log "Testing: groupId=$groupId, artifactId=$artifactId, version=$version, container=$container"
	else
		if [ ! -e $war ];then
			log "$war is not found" e t
			exit 1
		fi
		groupId=local
		artifactId=`basename $war`
		no_war_len=$((${#artifactId}-4))
		artifactId=${artifactId:0:$no_war_len}
		version=1.0
		log "Testing: war=$war"
	fi
	
	if [[ x$container != xjboss && x$container != xtomcat ]];then
		log "container should be tomcat or jboss" e t
		exit 1
	fi

	if [ x$container == xtomcat ];then
		if [ ! -e $TOMCAT_HOME/bin/startup.sh ];then
			log "TOMCAT_HOME does not point to a valid tomcat installation" e t
			exit 1
		else
			log "Using tomcat at $TOMCAT_HOME"
		fi
	else
		if [ ! -e $JBOSS_HOME/bin/run.sh ];then
			log "JBOSS_HOME does not point to a valid jboss installation" e t
			exit 1
		fi
	fi

	if [ ! -e ../phoenix-kernel ];then
		echo "this script should be run with absolute path or in phoenix project directory"
		exit 1
	fi
}

function init {

	MAX_HTTP_TRY=30
	PHOENIX_KERNEL_WAR=../phoenix-kernel/target/phoenix-kernel.war
	PHOENIX_KERNEL_TARGET=target/data/webapps/phoenix-kernel/
	PHOENIX_BOOTSTRAP_JAR=../phoenix-bootstrap/target/phoenix-bootstrap.jar
	export CATALINA_PID=target/data/tomcat6.pid
	
	rm -rf target
}

function retrive_and_unpack_war {
	wartmp=target/wartmp/$groupId/$artifactId/$version
	rm -rf $wartmp
	mkdir -p $wartmp

	if [ x$war == x ];then
		retrive_and_unpack_war_from_maven "$@"
	else
		retrive_and_unpack_war_from_local "$@"
	fi

	if [ x$container == xtomcat ];then
		mkdir $wartmp/$artifactId
		unzip $wartmp/*.$type -d $wartmp/$artifactId >/dev/null
	else
		jboss_biz_war_dir=$JBOSS_HOME/server/default/deploy/phoenix-biz.war	
		rm -rf $jboss_biz_war_dir
		mkdir -p $jboss_biz_war_dir
		unzip $wartmp/*.$type -d $jboss_biz_war_dir >/dev/null
		if [ ! -e $jboss_biz_war_dir/WEB-INF/jboss-web.xml ];then
			cat <<-END > $jboss_biz_war_dir/WEB-INF/jboss-web.xml
				<!DOCTYPE jboss-web PUBLIC "-//JBoss//DTD Web Application 5.0//EN"
				" http://www.jboss.org/j2ee/dtd/jboss-web_5_0.dtd">
				<jboss-web>
						<context-root>/</context-root>
				</jboss-web>
			END
		fi
	fi
}

function retrive_and_unpack_war_from_local {
	cp $war $wartmp/
}

function retrive_and_unpack_war_from_maven {
	# retrive war from maven repo
	log "Retriving $groupId:$artifactId:$version:$type from maven repo" i f f
	./maven.sh $wartmp $groupId $artifactId $version $type
	ls $wartmp/*.$type >/dev/null 2>&1
	if [ $? -eq 0 ];then
		log "Successfullt retrive $groupId:$artifactId:$version:$type from maven repo" i t
	else
		log "Failed to retrive $groupId:$artifactId:$version:$type from maven repo" e t
		exit 1
	fi
}

function package_phoenix {
	# package phoenix
	cd ..
	if [ uname == "Linux" ];then
		file_latest_mtime=`find phoenix-bootstrap phoenix-kernel -regextype posix-extended -regex ".*src/main/.*|.*.xml" -type f -printf "%T@\n" | sort -n | tail -1`
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
	if [ x$container == xtomcat ];then
		cp -rf $PHOENIX_BOOTSTRAP_JAR $TOMCAT_HOME/lib/
	else
		cp -rf $PHOENIX_BOOTSTRAP_JAR $JBOSS_HOME/server/default/lib/
	fi
}

function restart_container {
	if [ x$container == xtomcat ];then
		restart_tomcat
	else
		restart_jboss
	fi
}

function kill_jboss {
	jps -lvm | awk -v tocheck=$JBOSS_HOME '$2=="org.jboss.Main" && index($0, tocheck)>0{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function kill_tomcat {
	jps -lvm | awk -v tocheck=$TOMCAT_HOME '$2=="org.apache.catalina.startup.Bootstrap" && index($0, tocheck)>0{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function restart_jboss {
	kill_tomcat
	kill_jboss

	cat <<-END > $jboss_biz_war_dir/WEB-INF/context.xml
		<?xml version="1.0" encoding="UTF-8"?>
		<Context>
			<Loader className="com.dianping.phoenix.bootstrap.Jboss4WebappLoader" kernelDocBase="$cwd/$PHOENIX_KERNEL_TARGET" />
		 </Context>
	END

	$JBOSS_HOME/bin/run.sh &
}

function restart_tomcat {
	kill_tomcat
	kill_jboss

	cp $TOMCAT_HOME/conf/context.xml $TOMCAT_HOME/conf/context.xml.bak
	cat <<-END > $TOMCAT_HOME/conf/context.xml
		<?xml version='1.0' encoding='utf-8'?>
		<Context>
			<WatchedResource>WEB-INF/web.xml</WatchedResource>
		</Context>
	END

	# generate context xml file
	BIZ_WAR=$cwd/$wartmp/$artifactId
	rm -rf $TOMCAT_HOME/webapps/ROOT
	mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
	cat <<-END > $TOMCAT_HOME/conf/Catalina/localhost/ROOT.xml
		<?xml version="1.0" encoding="UTF-8"?>
		<Context docBase="$BIZ_WAR">
			<Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader" kernelDocBase="$cwd/$PHOENIX_KERNEL_TARGET" />
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
retrive_and_unpack_war "$@"
package_phoenix "$@"
install_phoenix "$@"
restart_container "$@"
self_check_war "$@"
qa_check_war "$@"
