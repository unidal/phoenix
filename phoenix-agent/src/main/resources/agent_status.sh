# TODO remove X_HOME or set it
TOMCAT_PID=`jps -lvm | awk -v tocheck=$TOMCAT_HOME '$2=="org.apache.catalina.startup.Bootstrap" && index($0, tocheck)>0{pid=$1;print pid;}'`
JBOSS_PID=`jps -lvm | awk -v tocheck=$JBOSS_HOME '$2=="org.jboss.Main" && index($0, tocheck)>0{pid=$1;print pid;}'`

while getopts "c:p:" option;do
	case $option in
			c)      container=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
			p)      container_install_path=$OPTARG;;
	esac
done

container_status=down
if [ x$container == xtomcat ];then
	if [ x$TOMCAT_PID != x ];then
		container_status=up
	fi
fi

if [ x$container == xtomcat ];then
	if [ x$JBOSS_PID != x ];then
		container_status=up
	fi
fi

echo $container_status

#jboss_root=`jps -lvm | awk -v tocheck=$JBOSS_HOME '$2=="org.jboss.Main" && index($0, tocheck)>0{cmd=$0;tofind="-Djava.endorsed.dirs=";idx=index(cmd,tofind);libdir=substr(cmd,idx+length(tofind));toremove="endorsed/lib";print substr(libdir,0,length(libdir)-length(toremove));}'`

