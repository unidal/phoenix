function add_ssh_private_key {
	ssh_config=~/.ssh/config
	local host_cnt=`grep -c $kernel_git_host $ssh_config 2>/dev/null || true`
	local write=0
	if [ x$host_cnt == x ];then	#config file not exist
		write=1
	else
		if [ $host_cnt -eq 0 ];then	#no config entry for kernel git host
			write=1
		fi
	fi
	if [ $write -eq 1  ];then
		mkdir -p ~/.ssh
		cp git/.ssh/id_rsa ~/.ssh/id_rsa.phoenix
		chmod 600 ~/.ssh/id_rsa.phoenix
		cat <<-END >> $ssh_config
			
			Host $kernel_git_host
			IdentityFile ~/.ssh/id_rsa.phoenix
		END
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

function get_kernel_war {
	log "getting kernel war from $kernel_git_url"

	add_ssh_private_key

	mkdir -p $KERNEL_WAR_TMP

	cd $KERNEL_WAR_TMP
	if [ -e $KERNEL_WAR_TMP/.git ];then
		log "found existing kernel, fetching kernel war"
		git fetch $kernel_git_url master
		git checkout $kernel_version
	else
		log "no existing kernel found, cloning kernel war"
		git clone $kernel_git_url $KERNEL_WAR_TMP
		git checkout $kernel_version
	fi
	cd - >/dev/null

	log "got kernel war from git"
}

function stop_all {
	./op_${env}.sh -o $func -b $container_install_path -c $container_type
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
	./op_${env}.sh -o $func -b $container_install_path -c $container_type
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
	log "rolling back kernel"
	git_rollback $domain_kernel_base
	log "kernel version $kernel_version rolled back"
	./op_${env}.sh -o put_container_online -b $container_install_path -c $container_type
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
	./op_${env}.sh -o put_container_online -b $container_install_path -c $container_type
}
