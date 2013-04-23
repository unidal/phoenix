# pull phoenix-console source and packge to RPM
# yum install rpm-build
# yum install rpmdevtools

set -e
set -u

if [ $# -lt 1 ];then
        echo "version needed"
        exit 1
fi

version=$1

PHOENIX_ROOT=~/phoenix
PHOENIX_DIR=~/phoenix/phoenix
TMP_DIR=~/tmp
RPM_SOURCE_NAME=phoenix-console-$version
PHOENIX_CONSOLE_INSTALL_DIR_NAME=phoenix-console

mkdir -p $PHOENIX_ROOT
cd $PHOENIX_ROOT
if [ ! -e $PHOENIX_DIR ]; then
        git clone https://github.com/dianping/phoenix.git
fi
cd -

# pull lastest phoenix code
cd $PHOENIX_DIR
git pull

# package it
mvn -Dmaven.test.skip clean package
cd -

# prepare console code
mkdir -p $TMP_DIR
cd $TMP_DIR
rm -rf *
mkdir -p $RPM_SOURCE_NAME/$PHOENIX_CONSOLE_INSTALL_DIR_NAME
unzip -d $RPM_SOURCE_NAME/$PHOENIX_CONSOLE_INSTALL_DIR_NAME $PHOENIX_DIR/phoenix-console/target/phoenix-console-*.war

# prepare config.xml
cat >$RPM_SOURCE_NAME/project.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<root>
	<project name="user-web" owner="qing.gu">
		<description>Sample Application</description>
		<hosts>
			<host ip="127.0.0.1" status="up">
				<war name="user-web" version="0.1-SNAPSHOT">
					<dependency groupId="com.dianping.cat" artifactId="cat-core" version="3.4" />
					<dependency groupId="opensymphony" artifactId="sitemesh" version="1.1" />
					<dependency groupId="org.apache.struts" artifactId="struts2-core" version="2.2.0" />
					<dependency groupId="org.apache.struts" artifactId="struts2-json-plugin" version="2.1.8" />
					<dependency groupId="org.springframework" artifactId="spring-web" version="3.0" />
				</war>
				<war name="kernel" version="0.1-SNAPSHOT">
					<dependency groupId="com.dianping.cat" artifactId="cat-core" version="4.1" />
					<dependency groupId="org.apache.struts" artifactId="struts2-json-plugin" version="2.2.0" />
					<dependency groupId="org.springframework" artifactId="spring-web" version="2.5.6" />
				</war>
			</host>
		</hosts>
	</project>
</root>
EOF

cat >$RPM_SOURCE_NAME/datasources.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<data-sources>
	<data-source id="phoenix">
				<maximum-pool-size>3</maximum-pool-size>
				<connection-timeout>1s</connection-timeout>
				<idle-timeout>10m</idle-timeout>
				<statement-cache-size>1000</statement-cache-size>
				<properties>
					<driver>com.mysql.jdbc.Driver</driver>
					<url>jdbc:mysql://192.168.26.80:3306/phoenix</url>
					<user>root</user>
					<password>root</password>
					<connectionProperties><![CDATA[useUnicode=true&autoReconnect=true]]></connectionProperties>
				</properties>
	</data-source>
</data-sources>
EOF

# prepare bootstrap
cp $PHOENIX_DIR/phoenix-bootstrap/target/phoenix-bootstrap.jar $RPM_SOURCE_NAME/

tar czf $RPM_SOURCE_NAME.tar.gz $RPM_SOURCE_NAME

rpmdev-setuptree
cp $RPM_SOURCE_NAME.tar.gz ~/rpmbuild/SOURCES/
cp $PHOENIX_DIR/phoenix-console/src/main/resources/rpm/phoenix-console.spec ~/rpmbuild/SPECS/
	
cd -

rpmbuild -bb ~/rpmbuild/SPECS/phoenix-console.spec

