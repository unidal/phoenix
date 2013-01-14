# To start phoenix-agent #
1. run "mvn -Dmaven.test.skip clean package" under phoenix top directory

2. copy phoenix-agent/src/test/resources/com/dianping/phoenix/configure/config.xml to /data/appdatas/phoenix/, set `<container-install-path>` to the correct path, set `<config env="">`, set `<jboss-server-name>`

3. copy phoenix-bootstrap.jar to container's lib directory

4. add domain `<Context>` to container's server.xml, make sure docBase ends with /current, remove server/default/deploy/jboss-web.deployer/ROOT.war/ if the path is "/" and the container is jboss
 
	`<Context docBase="/data/webapps/sample-app/current" path="/s">`

5. unzip phoenix-agent-0.1-SNAPSHOT.war to any directory X

6. chmod +x X/startup.sh

7. X/startup.sh

8. to verify, visit http://agent_ip:3473/phoenix/agent/deploy