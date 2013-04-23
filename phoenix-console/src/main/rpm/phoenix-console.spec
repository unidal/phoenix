#%define		name	value
%define __jar_repack 0


Name:		phoenix-console
Version:	0.1
Release:	1
Summary:	phoenix-console
Requires:	git

Group:		Development/Tools
License:	GPL
Source0:	%{name}-%{version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-XXXXXXXXXX)


%description
A powerful console to control phoenix agent


%prep
%setup -q


%build


%install
# clean rpm build root
[ -d $RPM_BUILD_ROOT ] && rm -rf $RPM_BUILD_ROOT/*

# where to install console files
CONSOLE_INSTALL_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-console
CONSOLE_CONFIG_DIR=$RPM_BUILD_ROOT/data/appdatas/phoenix

# create console directories
[ -d $CONSOLE_INSTALL_DIR ] || mkdir -p $CONSOLE_INSTALL_DIR
[ -d $CONSOLE_CONFIG_DIR ] || mkdir -p $CONSOLE_CONFIG_DIR

# copy console files to corresponding directories
cp -r phoenix-console/* $CONSOLE_INSTALL_DIR/
cp project.xml $CONSOLE_CONFIG_DIR
cp datasources.xml $CONSOLE_CONFIG_DIR

%post
# add user phoenix
usermod -a -G nobody phoenix
# change required file permissions
CONSOLE_INSTALL_DIR=$RPM_BUILD_ROOT/data/webapps/phoenix/phoenix-console
SERVER_XML=/usr/local/jboss/server/default/deploy/jboss-web.deployer/server.xml

[ -f $SERVER_XML ] && /bin/chown phoenix:phoenix $SERVER_XML


APPLOGS_DIR=/data/applogs
APPDATAS_DIR=/data/appdatas
[ -d $APPLOGS_DIR ] && chown nobody:nobody $APPLOGS_DIR && chmod 775 $APPLOGS_DIR
[ -d $APPDATAS_DIR ] && chown nobody:nobody $APPDATAS_DIR && chmod 775 $APPDATAS_DIR


# comment out Defaults requiretty to enabel sudo in scriptss'
echo "You need to add context path to jboss server xml: <Context docBase=\"$CONSOLE_INSTALL_DIR\" path=\"/phoenix\"></Context>"


%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,phoenix,phoenix,-)
/data/webapps/phoenix/phoenix-console
/data/appdatas/phoenix/datasources.xml
/data/appdatas/phoenix/project.xml
%doc


%changelog


%preun


%postun
rm -rf /data/webapps/phoenix/phoenix-console
rm -rf /data/appdatas/phoenix
