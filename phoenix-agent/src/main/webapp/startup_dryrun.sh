
set -e
set -u

cd `dirname $0`

function kill_by_javaclass {
	local javaclass=$1
	jps -lvm | awk -v javaclass=$javaclass '$2==javaclass{cmd=sprintf("kill -s TERM %s; sleep 1; kill -9 %s", $1, $1);system(cmd)}'
}

agent_class="com.dianping.phoenix.agent.PhoenixAgent"
port=3473

if [ $# -gt 1 ];then
	agent_class=$1
	port=$2
fi

kill_by_javaclass $agent_class

if [ -e WEB-INF/classes ];then
	rm -rf classes
	mv WEB-INF/classes ./
fi
if [ -e WEB-INF/lib ];then
	rm -rf lib
	mv WEB-INF/lib ./
fi

java=/usr/local/jdk/bin/java
if [ ! -x $java ];then
	java=java
fi

echo "Starting phoenix-agent $agent_class $port `pwd`"
$java -classpath classes:"lib/*" $agent_class $port /phoenix `pwd` >/dev/null 2>&1
