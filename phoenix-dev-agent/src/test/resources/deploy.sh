#!/usr/bin/expect

set IP [lindex $argv 0]
set PORT [lindex $argv 1]
set USER [lindex $argv 2]
set PWD [lindex $argv 3]
set WAR [lindex $argv 4]

spawn ssh -p$PORT $USER@$IP
expect "(yes/no)?" {
         send "yes\r"
         expect "password:" {
             send "$PWD\r"
         }
     } "password:" {
         send "$PWD\r"
     } 
expect "*]*"
send "chmod 777 -R /data/applogs\r"
expect "*]*"
send "cd /data/webapps/phoenix-dev-agent/phoenix.war/\r"
expect "*]*"
send "sed -i \"s/8080/7463/g\" /usr/local/jboss/server/default/deploy/jboss-web.deployer/server.xml\r"
expect "*war]*"
send "rm -rf *\r"
expect "*war]*"
send "wget $WAR -Osource.war\r"
expect "*war]*"
send "unzip source.war\r"
expect "*war]*"
send "rm -rf source.war\r" 
expect "*war]*"
send "service jboss restart\r"
expect "*war]*"
send "exit\r"
expect eof
exit
