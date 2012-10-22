TOMCAT_HOME=/Users/marsqing/Downloads/apache-tomcat-6.0.35/
SHOPPIC_WAR=/Users/marsqing/Projects/shoppic-service/shoppic-remote-service/target/shoppic-service.war

MAX_HTTP_TRY=30
LOG_HOME=/data/applogs/
PHOENIX_KERNEL_WAR=../phoenix-kernel/target/phoenix-kernel.war
PHOENIX_KERNEL_TARGET=/data/webapps/phoenix-kernel/
PHOENIX_BOOTSTRAP_JAR=../phoenix-bootstrap/target/phoenix-bootstrap.jar
export CATALINA_PID=/data/tomcat6.pid

# argements:
#	message
#	level: i, w, e
#	output from line beginning: t, f
#	output tailing newline: t, f
function log {
	color="[32m"
	if [ x$2 == "xw" ];then
		color="[33m"
	elif [ x$2 == "xe" ];then
		color="[31m"
	fi

	if [ x$3 == "xt" ];then
		echo -en "\r\033[K"
	fi

	newline=""
	if [ x$4 == "xf" ];then
		newline=" -n "
	fi
	
	echo $newline $color$1[0m
}

# package phoenix
cwd=`pwd`
if [ $# -eq 0 ];then
	cd ..
	log "Packaging phoenix..." i f f
	mvn -Dmaven.test.skip clean package > maven.log
	if [ $? -ne 0 ];then
		log "Error package phoenix" e t
		exit 1
	fi
	log "Package done" i t
	cd $cwd
fi

# copy phoenix kernel, bootstrap and custom web.xml to the right place
rm -rf $PHOENIX_KERNEL_TARGET
mkdir -p $PHOENIX_KERNEL_TARGET
unzip $PHOENIX_KERNEL_WAR -d $PHOENIX_KERNEL_TARGET >/dev/null
cp phoenix-web.xml $PHOENIX_KERNEL_TARGET/WEB-INF/classes/
cp alpaca-client-0.2.2.jar $PHOENIX_KERNEL_TARGET/WEB-INF/lib/
cp -rf $PHOENIX_BOOTSTRAP_JAR $TOMCAT_HOME/lib/

# stop all tomcat
jps |awk '$2=="Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'

# generate context xml file
mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
cat <<-END > $TOMCAT_HOME/conf/Catalina/localhost/shoppic.xml
<?xml version="1.0" encoding="UTF-8"?>
<Context docBase="$SHOPPIC_WAR">
   <Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader" kernelDocBase="$PHOENIX_KERNEL_TARGET" debug="true" />
 </Context>
END


# clear dirs
rm -rf $LOG_HOME/*
rm -rf $TOMCAT_HOME/logs/*

# start tomcat
log "Starting tomcat..." i f f
$TOMCAT_HOME/bin/startup.sh >/dev/null
sleep 5
tomcat_pid=`cat $CATALINA_PID`
ps_result_row=`ps -p $tomcat_pid | wc -l`
if [ $ps_result_row -eq 2 ];then
	log "Tomcat successfully started" i t
else
	log "Tomcat failed to start" e t
	exit 1
fi
log "Errors in catalina.out" w
grep -Ei "ERROR|SEVERE" $TOMCAT_HOME/logs/catalina.out

log "Starting test web app..." i f f
i=0
shoppic_started="false"
while [ $i -lt $MAX_HTTP_TRY ]; do 
	i=$((i+1))
	curl -I http://127.0.0.1:8080/shoppic/index.jsp >/dev/null 2>&1
	if [ $? == 7 ]; then
		echo -n "."
		sleep 1
	else
		shoppic_started="true"
		echo ""
		break
	fi
done

if [ $shoppic_started == "true" ]; then
	res_code=`curl -I http://127.0.0.1:8080/shoppic/index.jsp 2>/dev/null | head -n1 | awk '{print $2}'`
	level="e"
	if [ x$res_code == "x200" ];then
		level="i"
	fi
	log $res_code $level t
else
	log "shoppic failed to start after $MAX_HTTP_TRY seconds" e t
fi
