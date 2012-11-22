#TOMCAT_ROOT=
TOMCAT_WEBAPP_ROOT=/Volumes/HDD2/tmp/log
TOMCAT_PID=`jps -lvm | awk -v tocheck=$TOMCAT_HOME '$2=="org.apache.catalina.startup.Bootstrap" && index($0, tocheck)>0{pid=$1;print pid;}'`


#JBOSS_ROOT=
#JBOSS_WEBAPP_ROOT=
JBOSS_PID=`jps -lvm | awk -v tocheck=$JBOSS_HOME '$2=="org.jboss.Main" && index($0, tocheck)>0{pid=$1;print pid;}'`

if [ x$TOMCAT_PID != x ];then
	echo "found tomcat"
fi
if [ x$JBOSS_PID != x ];then
	echo "found jboss"
fi

jboss_root=`jps -lvm | awk -v tocheck=$JBOSS_HOME '$2=="org.jboss.Main" && index($0, tocheck)>0{cmd=$0;tofind="-Djava.endorsed.dirs=";idx=index(cmd,tofind);libdir=substr(cmd,idx+length(tofind));toremove="endorsed/lib";print substr(libdir,0,length(libdir)-length(toremove));}'`

function inspect_jar {
	artifactId=`basename $1`	
	echo -n "'artifactId':'$artifactId'"
}

#find -E $TOMCAT_WEBAPP_ROOT -regex "$TOMCAT_WEBAPP_ROOT/[^/]*/META-INF/maven/.*/pom.properties" -exec cat {} \; | awk -F "=" 'NF==2{printf("%s=%s\n",$1,$2);}'
webinfs=`find -E $TOMCAT_WEBAPP_ROOT -regex "$TOMCAT_WEBAPP_ROOT/[^/]*/WEB-INF"`
echo -n "'wars':{"
first_war=true
for webinf in $webinfs;do
	if [ $first_war == true ];then
		first_war=false
	else
		echo -n ","
	fi
	war_root=`dirname $webinf`
	war_name=`basename $war_root`
	echo -n "'$war_name':{'name':$war_name,'libs':["
	#find -E $war_root -regex "$war_root/META-INF/maven/.*/pom.properties" -exec cat {} \; | awk -F "=" 'NF==2{printf("%s=%s\n",$1,$2);}'
	jars=`find -E $war_root -regex "$war_root/WEB-INF/lib/.*\.jar"`
	first_jar=true
	for jar in $jars;do
		if [ $first_jar == true ];then
			first_jar=false
		else
			echo -n ","
		fi
		echo -n "{"
		inspect_jar $jar
		echo -n "}"
	done	
	echo -n "]}"
done
echo "}"
