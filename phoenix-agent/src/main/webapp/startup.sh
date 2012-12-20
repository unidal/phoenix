cd `dirname $0`

if [ -e WEB-INF/classes ];then
	mv WEB-INF/classes ./
fi
if [ -e WEB-INF/lib ];then
	mv WEB-INF/lib ./
fi

command -v java >/dev/null 2>&1 || { echo >&2 "java is not found, put java on PATH"; exit 1; }

echo "Starting phoenix-agent"
nohup java -classpath classes:"lib/*" com.dianping.phoenix.agent.StandaloneServer 3473 /phoenix `pwd` >/dev/null 2>&1 &
echo "Started"
