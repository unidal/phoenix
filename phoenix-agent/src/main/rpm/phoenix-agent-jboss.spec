#%define		name	value
%define __jar_repack 0


Name:		phoenix-agent-jboss
Version:	0.1
Release:	1
Summary:	phoenix-agent-jboss
Requires:	git

Group:		Development/Tools
License:	GPL
Source0:	%{name}-%{version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-XXXXXXXXXX)


%description
A powerful customized J2EE web container (JBoss, Jetty, Tomcat)


%prep
%setup -q


%build


%install

[ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT/*

# where to install agent files
AGENT_INSTALL_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-agent
AGENT_CONFIG_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-config
BOOTSTRAP_JAR_DIR=$RPM_BUILD_ROOT/usr/local/jboss/server/default/lib/


# create agent directories
[ -d $AGENT_INSTALL_DIR ] || mkdir -p $AGENT_INSTALL_DIR
[ -d $AGENT_CONFIG_DIR ] || mkdir -p $AGENT_CONFIG_DIR
[ -d $BOOTSTRAP_JAR_DIR ] || mkdir -p $BOOTSTRAP_JAR_DIR

# copy agent files to corresponding directories
cp -r phoenix-agent/* $AGENT_INSTALL_DIR/
chmod +x $AGENT_INSTALL_DIR/startup.sh
cp config.xml $AGENT_CONFIG_DIR
cp phoenix-bootstrap.jar $BOOTSTRAP_JAR_DIR



%post
# add user phoenix
usermod -a -G nobody phoenix
# change required file permissions

SERVER_XML=/usr/local/jboss/server/default/deploy/jboss-web.deployer/server.xml
SERVER_XML_DIR=/usr/local/jboss/server/default/deploy/jboss-web.deployer/
JBOSS_SERVICE_XML=/usr/local/jboss/server/default/conf/jboss-service.xml

[ -f $SERVER_XML ] && /bin/chown phoenix:phoenix $SERVER_XML
[ -d $SERVER_XML_DIR ] && /bin/chown phoenix:phoenix $SERVER_XML_DIR
[ -f $JBOSS_SERVICE_XML ] && /bin/chown phoenix:phoenix $JBOSS_SERVICE_XML


APPLOGS_DIR=/data/applogs
APPDATAS_DIR=/data/appdatas
PHOENIX_ROOT_DIR=/data/webapps/phoenix/
[ -d $APPLOGS_DIR ] && chown nobody:nobody $APPLOGS_DIR && chmod 775 $APPLOGS_DIR
[ -d $APPDATAS_DIR ] && chown nobody:nobody $APPDATAS_DIR && chmod 775 $APPDATAS_DIR
[ -d $PHOENIX_ROOT_DIR ] && chown -R phoenix:phoenix $PHOENIX_ROOT_DIR

# comment out Defaults requiretty to enabel sudo in scripts
awk 'BEGIN{result=""}{if(match($0, "^[^#]*Defaults[[:space:]]+requiretty")>0){result=sprintf("%s#%s\n",result,$0);}else{result=sprintf("%s%s\n",result,$0);}}END{print result > "/etc/sudoers"}' /etc/sudoers


%clean
rm -rf $RPM_BUILD_ROOT


%files
%defattr(-,phoenix,phoenix,-)
/data/webapps/phoenix/phoenix-agent
/data/webapps/phoenix/phoenix-config
%attr(-, root, root) /usr/local/jboss/server/default/lib/phoenix-bootstrap.jar
%doc


%changelog


%preun
/usr/local/jdk/bin/jps -lvm | awk '$2=="com.dianping.phoenix.agent.PhoenixAgent"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'


%postun
/usr/local/jdk/bin/jps -lvm | awk '$2=="com.dianping.phoenix.agent.PhoenixAgent"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
rm -rf /data/webapps/phoenix/phoenix-agent
rm -rf /data/webapps/phoenix/phoenix-config
