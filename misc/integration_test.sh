
user=`whoami`
TOMCAT_HOME=${TOMCAT_HOME:="/Users/$user/Downloads/apache-tomcat-6.0.35/"}

cd `dirname $0`
cwd=`pwd`
source util.sh

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

groupId=$1
artifactId=$2
version=$3
forkGrep=t
if [ $# -ge 4 ];then
	forkGrep=$4
fi
type=war

rm -f test.log

# retrive war from maven repo
wartmp=target/wartmp/$groupId/$artifactId/$version
rm -rf $wartmp
mkdir -p $wartmp
log "Retriving $groupId:$artifactId:$version:$type from maven repo" i f f
./maven.sh $wartmp $groupId $artifactId $version $type >> test.log
mkdir $wartmp/$artifactId
ls $wartmp/*.$type >/dev/null 2>&1
if [ $? -eq 0 ];then
	log "Successfullt retrive $groupId:$artifactId:$version:$type from maven repo" i t
else
	log "Failed to retrive $groupId:$artifactId:$version:$type from maven repo" e t
	exit 1
fi
unzip $wartmp/*.$type -d $wartmp/$artifactId >>test.log

BIZ_WAR=$cwd/$wartmp/$artifactId

MAX_HTTP_TRY=30
LOG_HOME=/data/applogs/
PHOENIX_KERNEL_WAR=../phoenix-kernel/target/phoenix-kernel.war
PHOENIX_KERNEL_TARGET=/data/webapps/phoenix-kernel/
PHOENIX_BOOTSTRAP_JAR=../phoenix-bootstrap/target/phoenix-bootstrap.jar
export CATALINA_PID=/data/tomcat6.pid

uname=`uname`

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
	mvn  -Dmaven.test.skip clean package >> misc/test.log
	if [ $? -ne 0 ];then
		log "Error package phoenix" e t
		exit 1
	fi
	log "Package done" i t
else
	log "No new file found in phoenix-kernel or phoenix-bootstrap, skip packaging"
fi
cd - >/dev/null

# copy phoenix kernel, bootstrap and custom web.xml to the right place
rm -rf $PHOENIX_KERNEL_TARGET
mkdir -p $PHOENIX_KERNEL_TARGET
unzip $PHOENIX_KERNEL_WAR -d $PHOENIX_KERNEL_TARGET >/dev/null
cp -rf $PHOENIX_BOOTSTRAP_JAR $TOMCAT_HOME/lib/

# stop all tomcat
jps |awk '$2=="Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'

# generate context xml file
rm -rf $TOMCAT_HOME/webapps/ROOT
mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
cat <<-END > $TOMCAT_HOME/conf/Catalina/localhost/ROOT.xml
<?xml version="1.0" encoding="UTF-8"?>
<Context docBase="$BIZ_WAR">
   <Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader" kernelDocBase="$PHOENIX_KERNEL_TARGET" debug="true" />
 </Context>
END


# clear dirs
rm -rf $LOG_HOME/*
rm -rf $TOMCAT_HOME/logs/*

# start tomcat
log "Starting tomcat..." i f f
$TOMCAT_HOME/bin/startup.sh >/dev/null
sleep 1
tomcat_pid=`cat $CATALINA_PID`
ps_result_row=`ps -p $tomcat_pid | wc -l`
if [ $ps_result_row -eq 2 ];then
	log "Tomcat successfully started" i t
else
	log "Tomcat failed to start" e t
	exit 1
fi

log "Start testing web app..." i f f
i=0
biz_started="false"
while [ $i -lt $MAX_HTTP_TRY ]; do 
	i=$((i+1))
	curl -I http://127.0.0.1:8080/index.jsp >/dev/null 2>&1
	if [ $? == 7 ]; then
		echo -n "."
		sleep 1
	else
		biz_started="true"
		echo ""
		break
	fi
done

exit_code=0
if [ $biz_started == "true" ]; then
	res_code=`curl -I http://127.0.0.1:8080/index.jsp 2>/dev/null | head -n1 | awk '{print $2}'`
	level="e"
	if [ x$res_code == "x200" ];then
		level="i"
	else
		exit_code=1
	fi
	log "HTTP response code is $res_code" $level t
else
	exit_code=1
	log "$artifactId failed to start after $MAX_HTTP_TRY seconds" e t
fi

#log "Errors in catalina.out" w
grep -E "ERROR|Error|SEVERE" $TOMCAT_HOME/logs/catalina.out
if [ $forkGrep == "t" ];
then
	log "Press Ctrl-C to exit"
	tail -f $TOMCAT_HOME/logs/catalina.out | grep -E "ERROR|Error|SEVERE"
fi
exit $exit_code
