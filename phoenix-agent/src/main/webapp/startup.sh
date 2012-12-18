cd `dirname $0`

if [ -e WEB-INF/classes ];then
	mv WEB-INF/classes ./
fi
if [ -e WEB-INF/lib ];then
	mv WEB-INF/lib ./
fi
java -classpath classes:"lib/*" com.dianping.phoenix.agent.StandaloneServer 3473 /phoenix `pwd`
