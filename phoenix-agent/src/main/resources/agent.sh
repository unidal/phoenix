set -e
set -u
#trap "echo 'INT signal received'" INT
#trap "echo 'TERM signal received'" TERM
cd `dirname $0`
function log {
	echo "[`date +'%m-%d %H:%M:%S'`] [INFO] $@"
}

KERNEL_WAR_BASE=/data/webapps/phoenix-kernel/

KERNEL_WAR_TMP=/data/appdatas/phoenix_kernel_war_tmp/
KERNEL_GIT_HOST="10.1.4.81"
KERNEL_GIT_URL="ssh://git@${KERNEL_GIT_HOST}:58422/phoenix-kernel.git"

now=`date "+%Y-%m-%d"`
while getopts "x:c:d:v:f:" option;do
	case $option in
			c)		container=$OPTARG;;
			d)      domain=$OPTARG;;
			v)      kernel_version=$OPTARG;;
			f)      func=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
			x)      server_xml=$OPTARG;;
	esac
done
domain_kernel_base=$KERNEL_WAR_BASE/$domain

function add_ssh_private_key {
	ssh_config=~/.ssh/config
	local host_cnt=`grep -c $KERNEL_GIT_HOST $ssh_config 2>/dev/null || true`
	local write=0
	if [ x$host_cnt == x ];then	#config file not exist
		write=1
	else
		if [ $host_cnt -eq 0 ];then	#no config entry for kernel git host
			write=1
		fi
	fi
	if [ $write -eq 1  ];then
		cp git/.ssh/id_rsa ~/.ssh/id_rsa.phoenix
		chmod 600 ~/.ssh/id_rsa.phoenix
		cat <<-END >> $ssh_config
			
			Host $KERNEL_GIT_HOST
			 IdentityFile ~/.ssh/id_rsa.phoenix
		END
	fi
}

log "PID is $$"
log "CMD is $0 $@"

function exit_on_error {
	if [ $? -ne 0 ];then
		if [ ! x$1 == x ];then
			log $1
		fi
		exit 1
	fi
}

function init {
	# set up a git repo for server.xml to enable rollback/commit
	server_xml_dir=`dirname $server_xml`
	if [ ! -e $server_xml_dir/.git ];then
		cd $server_xml_dir
		git init
		git add server.xml
		git commit -m "init commit"
		cd - > /dev/null
	fi
}

function kill_jboss {
	jps -lvm | awk '$2=="org.jboss.Main"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function kill_tomcat {
	jps -lvm | awk '$2=="org.apache.catalina.startup.Bootstrap"{cmd=sprintf("kill -9 %s", $1);system(cmd)}'
}

function get_kernel_war {
	log "getting kernel war from git"
	add_ssh_private_key
	rm -rf $KERNEL_WAR_TMP
	mkdir -p $KERNEL_WAR_TMP
	git clone $KERNEL_GIT_URL $KERNEL_WAR_TMP
	cd $KERNEL_WAR_TMP
	git checkout -b dummy $kernel_version
	cd - >/dev/null
	log "got kernel war from git"
}

function turn_off_traffic {
	log "turning off traffic"
	log "traffic turned off"
}

function stop_container {
	log "stopping container"
	kill_jboss
	kill_tomcat
	log "container stopped"
}

function upgrade_kernel {
	log "upgrading $domain kernel to $kernel_version"
	# no kernel installed for domain
	if [ ! -e $domain_kernel_base ];then
		mkdir -p $domain_kernel_base
		# to avoid warning when git reset
		touch $domain_kernel_base/dummy
	fi

	# make kernel a git repo if not yet
	if [ ! -e $domain_kernel_base/.git ];then
		cd $domain_kernel_base
		git init
		git add .
		git commit -am "init commit"
		cd - >/dev/null
	fi

	rm -rf $domain_kernel_base/*
	cp -r $KERNEL_WAR_TMP/* $domain_kernel_base/
	cd $domain_kernel_base
	git add .
	cd - >/dev/null
	log "$domain kernel upgraded to $kernel_version"
}

function start_container {
	log "starting container"
	/Users/marsqing/Downloads/apache-tomcat-6.0.35/bin/startup.sh
	log "container started"
}

function check_container_status {
	log "checking container status"
	log "container status ok"
}

# Rollback a git directory
# Parameters: 1. git_directory
function git_rollback {
	local git_dir=$1
	cd $git_dir
	git reset --hard
	cd - >/dev/null
}

function rollback {
	# TODO rollback all other things
	log "rolling back kernel"
	git_rollback $domain_kernel_base
	log "kernel version $kernel_version rolled back"
	turn_on_traffic "$@"
}

# Commit a git directory
# Parameters: 1. git_directory, 2. comment
function git_commit {
	local git_dir=$1
	local comment=$2
	cd $git_dir
	local change_files=`git status --short --untracked-files=no | wc -l`
	if [ $change_files -gt 0 ];then
		log "committing $change_files files"
		git commit -am "$comment"
	else
		log "no file changed, no commit necessary"
	fi
	cd - >/dev/null
}

function commit {
	# commit kernel
	log "committing kernel for $domain in $domain_kernel_base"
	git_commit $domain_kernel_base "update to $kernel_version"
	log "committed"

	# commit server.xml
	log "committing server.xml"
	git_commit `dirname $server_xml` "update to $kernel_version"
	log "committed"

	# turn on traffic
	turn_on_traffic "$@"
}

function turn_on_traffic {
	log "turning on traffic"
	log "traffic turned on"
}

function ensure_not_empty {
	for var in "$@";do
		IFS='=' read -ra KV <<< "$var"
		if [ ${#KV[*]} -eq 1 ];then
			log "${KV[0]} is required"
			exit 1
		fi
	done
}

ensure_not_empty domain="$domain" version="$kernel_version"

$func

#get_kernel_war
#turn_off_traffic
#stop_container
#upgrade_kernel
#start_container
#check_container_status
#commit
