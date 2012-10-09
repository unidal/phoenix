TOMCAT_HOME=/Users/marsqing/Downloads/apache-tomcat-6.0.35/
SHOPPIC_WAR=/Users/marsqing/Projects/shoppic-service/shoppic-remote-service/target/shoppic-service-dev-3.3.5.war
MAX_HTTP_TRY=30

WEB_LOG_HOME=/data/applogs/

# stop all tomcat
jps |awk '$2=="Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'

# generate context xml file
mkdir -p $TOMCAT_HOME/conf/Catalina/localhost/
cat <<-END > $TOMCAT_HOME/conf/Catalina/localhost/shoppic.xml
<?xml version="1.0" encoding="UTF-8"?>
<Context docBase="$SHOPPIC_WAR">
   <Loader className="org.apache.catalina.loader.VirtualWebappLoader"
              virtualClasspath=""/>
 </Context>
END


# clear dirs
rm -rf $WEB_LOG_HOME/*
rm -rf $TOMCAT_HOME/logs/*

# start tomcat
$TOMCAT_HOME/bin/startup.sh

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
	echo $res_code
else
	echo "shoppic failed to start after $MAX_HTTP_TRY seconds"
fi
