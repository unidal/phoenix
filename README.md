Phoenix
=======
Phoenix is a powerful custmized container for java application server(eg. JBoss, Jetty, Tomcat), created and maintained by [Frankie Wu](http://github.com/unidal), [marsqing](http://github.com/marsqing), [Bin Miao](http://github.com/firefox007), [Figo Yang](http://github.com/figoyang) and [Jian Liu](http://github.com/liuliliujian).

Get Started
-----------

Prerequisite: 

* JDK 1.6.0 or above
* Maven 3.0.3 or above 
* Tomcat 6 or JBoss 4

Installation: 

	git clone git@github.com:dianping/phoenix.git

	cd phoenix/phoenix-samples/
	mvn install
	cd ..

Run with Tomcat:

	export TOMCAT_HOME={TOMCAT_HOME}
	misc/integration_test.sh -g com.dianping.platform -a sample-app1 -v 0.1-SNAPSHOT -c tomcat

Run with JBoss: 

	export JBOSS_HOME={JBOSS_HOME}
	misc/integration_test.sh -g com.dianping.platform -a sample-app1 -v 0.1-SNAPSHOT -c jboss
	
Check:

	open http://localhost:8080
	open http://localhost:8080/inspect/home

Attentions:

* Using DianPing private maven repository
* Currently only tested under Mac OSX 10.8

Author
------
* Frankie Wu
	* <http://weibo.com/u/2203580833> 
	* <http://github.com/unidal>
* marsqing
	* <http://weibo.com/marsqing>
	* <http://github.com/marsqing>
* Bin Miao
	* <http://weibo.com/u/2085273381>
	* <http://github.com/firefox007>
* Figo Yang
	* <http://weibo.com/figoyang>
	* <http://github.com/figoyang>
* Jian Liu
	* <http://weibo.com/1947958763>
	* <http://github.com/liuliliujian> 

Copyright and license
---------------------
Copyright 2012 DianPing, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
