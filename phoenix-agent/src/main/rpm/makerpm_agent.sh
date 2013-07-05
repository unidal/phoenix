# pull phoenix-agent source and packge to RPM
# yum install rpm-build
# yum install rpmdevtools

set -e
set -u

if [ ! $# -eq 2 ];then
        echo "Usage: makerpm.sh tomcat/jboss version."
        exit 1
fi

container=$1
version=$2

if [ ! "$1" = "tomcat" ] && [ ! "$1" = "jboss" ]; then
	echo "Usage: makerpm.sh tomcat/jboss version."
        echo "Container can only be tomcat or jboss."
        exit 1
fi

PHOENIX_ROOT=~/phoenix
PHOENIX_DIR=~/phoenix/phoenix
TMP_DIR=~/tmp
RPM_SOURCE_NAME=phoenix-agent-$container-$version
PHOENIX_AGENT_INSTALL_DIR_NAME=phoenix-agent

mkdir -p $PHOENIX_ROOT
cd $PHOENIX_ROOT
if [ ! -e $PHOENIX_DIR ]; then
        git clone https://github.com/dianping/phoenix.git
fi
cd -

# pull lastest phoenix code
cd $PHOENIX_DIR/phoenix-agent
git pull

# package it
mvn -Dmaven.test.skip clean package
cd -

# prepare agent code
mkdir -p $TMP_DIR
cd $TMP_DIR
rm -rf *
mkdir -p $RPM_SOURCE_NAME/$PHOENIX_AGENT_INSTALL_DIR_NAME
unzip -d $RPM_SOURCE_NAME/$PHOENIX_AGENT_INSTALL_DIR_NAME $PHOENIX_DIR/phoenix-agent/target/phoenix-agent-*.war

# prepare config.xml

cat >$RPM_SOURCE_NAME/config.xml <<EOF
        <config env="product">
        	<agent>
                	<container-install-path><![CDATA[/usr/local/$container]]></container-install-path>
        	</agent>
        </config>
EOF


# prepare bootstrap
cd $PHOENIX_DIR/phoenix-bootstrap
mvn -Dmaven.test.skip clean package
cd -
cp $PHOENIX_DIR/phoenix-bootstrap/target/phoenix-bootstrap.jar $RPM_SOURCE_NAME/

tar czf $RPM_SOURCE_NAME.tar.gz $RPM_SOURCE_NAME


rpmdev-setuptree
cp $RPM_SOURCE_NAME.tar.gz ~/rpmbuild/SOURCES/
cp $PHOENIX_DIR/phoenix-agent/src/main/rpm/phoenix-agent-$container.spec ~/rpmbuild/SPECS/

cd -

rpmbuild -bb ~/rpmbuild/SPECS/phoenix-agent-$container.spec


