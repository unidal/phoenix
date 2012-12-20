# to start phoenix-agent 
1. run "mvn -Dmaven.test.skip clean package" under phoenix top directory

2. copy phoenix-agent/src/test/resources/com/dianping/phoenix/configure/config.xml to /data/appdatas/phoenix/ and set <container-install-path> to the corrent path

3. copy phoenix-bootstrap.jar to container's lib directory

4. add domain <Context> to container's server.xml, make sure docBase ends with /current
	<Context docBase="/data/webapps/sample-app/current" path="/s">

5. unzip phoenix-agent-0.1-SNAPSHOT.war to a directory X

6. chmod +x X/startup.sh

7. X/startup.sh
