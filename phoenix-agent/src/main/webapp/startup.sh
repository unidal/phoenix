
set -e
set -u

cd `dirname $0`

if [ -e WEB-INF/classes ];then
	mv WEB-INF/classes ./
fi
if [ -e WEB-INF/lib ];then
	mv WEB-INF/lib ./
fi

java=/usr/local/jdk/bin/java
if [ ! -x $java ];then
	java=java
fi

echo "Starting phoenix-agent"
nohup $java -classpath classes:"lib/*" com.dianping.phoenix.agent.StandaloneServer 3473 /phoenix `pwd` >/dev/null 2>&1 &
echo "Started"
