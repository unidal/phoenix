
# pull phoenix-agent source and packge to RPM
# yum install rpmbuild
# yum install rpmdevtools
# rpmdev-setuptree


set -e
set -u

if [ $# -lt 1 ];then
	echo "version needed"
	exit 1
fi

version=$1

PHOENIX_DIR=/root/phoenix
TMP_DIR=/root/tmp
RPM_SOURCE_NAME=phoenix-agent-$version
PHOENIX_AGENT_INSTALL_DIR_NAME=phoenix-agent

mkdir -p $PHOENIX_DIR
cd $PHOENIX_DIR
git clone https://github.com/dianping/phoenix.git
cd -

# pull lastest phoenix code
cd $PHOENIX_DIR
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
	</config>
EOF

# prepare bootstrap
cp $PHOENIX_DIR/phoenix-bootstrap/target/phoenix-bootstrap.jar $RPM_SOURCE_NAME/

tar czf $RPM_SOURCE_NAME.tar.gz $RPM_SOURCE_NAME
cp $RPM_SOURCE_NAME.tar.gz ~/rpmbuild/SOURCES/
cd -

#rpmbuild -bb path_to_phoenix-agent.spec 
