#!/bin/bash

source ./util.sh

function add_ssh_private_key {
	local git_host=$1

	ssh_config=~/.ssh/config
	local host_cnt=`grep -c $git_host $ssh_config 2>/dev/null || true`
	local write=0
	if [ x$host_cnt == x ];then	#config file not exist
		log ".ssh/config not found"
		write=1
	else
		if [ $host_cnt -eq 0 ];then	#no config entry for kernel git host
			log "no phoenix private key found in .ssh/config"
			write=1
		fi
	fi
	if [ $write -eq 1  ];then
		log "try to add phoenix private key to .ssh/config"
		mkdir -p ~/.ssh
		cp -r git/.ssh/id_rsa ~/.ssh/id_rsa.phoenix
		chmod 600 ~/.ssh/id_rsa.phoenix
		cat <<-END >> $ssh_config
			
			Host $git_host
			IdentityFile ~/.ssh/id_rsa.phoenix
			StrictHostKeyChecking no
		END
		chmod 600 $ssh_config
		log "phoenix private key added to .ssh/config"
	fi
}

function create_git_repo {
	local git_dir=$1
	if [ ! -e $git_dir/.git ];then
		log "no .git directory found in $git_dir, make it a git repo"
		cd $git_dir
		git init
		git add .
		git commit -m "init commit"
		cd - > /dev/null
		log "$git_dir now a git repo"
	fi
}

function sync_git_repo {

	local git_url=$1
	shift
	local tag=$1
	shift
	local dest_dir=$1

	log "sync git repo at $git_url tag $tag to $dest_dir"
	mkdir -p $dest_dir

	cd $dest_dir
	if [ -e $dest_dir/.git ];then
		log "found existing repo, fetching update"
		git reset --hard
		git fetch --tags $git_url master
		git checkout $tag
	else
		log "no repo found, cloning"
		git clone $git_url $dest_dir
		git checkout $tag
	fi
	cd - >/dev/null

	log "repo synced from git"
}

function upgrade_local_git_repo {

	local src_dir=$1
	shift
	local dest_dir=$1

	log "upgrading $dest_dir from $src_dir"

	if [ ! -e $dest_dir ];then
		mkdir -p $dest_dir
		# to avoid warning when git reset
		touch $dest_dir/dummy
	fi

	create_git_repo $dest_dir	

	rm -rf $dest_dir/*
	cp -r $src_dir/* $dest_dir/
	cd $dest_dir
	git add .
	cd - >/dev/null
	log "$dest_dir upgraded"
}

# Rollback a git directory
# Parameters: 1. git_directory
function git_rollback {
	local git_dir=$1
	cd $git_dir
	git reset --hard
	cd - >/dev/null
}

# Commit a git directory
# Parameters: 1. git_directory, 2. comment
function git_commit {
	local git_dir=$1
	shift
	local comment=$1
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

function kill_by_javaclass {
	local javaclass=$1	

	jps -lvm | awk -v javaclass=$javaclass '$2==javaclass{cmd=sprintf("kill -s TERM %s; sleep 1; kill -9 %s", $1, $1);system(cmd)}'
}

function change_status {
    cd `dirname $tx_log_file`
    awk -v REPLACE_WORD=$1 '{if(/^txJson/){sub(/PROCESSING/,REPLACE_WORD,$1);print $1;} else {print $1;}}' tx.properties > tx.properties.tmp && mv tx.properties.tmp tx.properties
}

function tag_separator {
	echo -e "\r--9ed2b78c112fbd17a8511812c554da62941629a8--\r"
}

function tag_terminater {
	echo -e "--255220d51dc7fb4aacddadedfe252a346da267d4--\r"
}

function tag_success {
        tag_separator
        echo -e "Status: successful\r"
        echo -e "Step: SUCCESS\r"
        tag_separator
        change_status "SUCCESS"
}

function tag_failed {
        tag_separator
        echo -e "Status: failed\r"
        echo -e "Step: FAILED\r"
        tag_separator
        change_status "FAILED"
}

function ensure_agent_started {
    log "checking whether agent process alive"
    agent_process_num=`jps -lvm | awk -v javaclass=$agent_class '$2==javaclass{print $0}' | wc -l`
    if [ $agent_process_num -eq 0 ];then
        log_error "no agent process found, try to git reset agent dir and start it"
        cd $agent_doc_base
        git reset --hard
        chmod +x $agent_doc_base/startup.sh
        $agent_doc_base/startup.sh
        tag_failed
        cd - > /dev/null
    else
        log "agent process is alive"
        tag_success
    fi
    echo -e "\r"
    tag_terminater
}

function gitpull {
    sleep 1
    add_ssh_private_key $agent_git_host
    sync_git_repo $agent_git_url $agent_version $agent_war_tmp
}

function dryrun {
    kill_by_javaclass $agent_dryrun_class
    new_agent_ok=true
    chmod +x $agent_war_tmp/startup_dryrun.sh
    $agent_war_tmp/startup_dryrun.sh $agent_dryrun_class $dry_run_port || { new_agent_ok=false; }
    if [ $new_agent_ok != true ]; then
        log_error "new agent is corrputed, won't update"
        exit 1
    fi
}

function upgrade {
    trap ensure_agent_started EXIT
    log "new agent is valid, replace current agent"
    if [ -f $tx_log_file ]; then
        exec 1>>$tx_log_file 2>&1
    fi
    log "shutting down old agent"

    kill -15 $parent_pid || sleep 1 || kill -9 $parent_pid

    log "updating phoenix agent"
    upgrade_local_git_repo $agent_war_tmp $agent_doc_base
    git_commit $agent_doc_base "agent upgraded to $agent_version"
    chmod +x $agent_doc_base/startup.sh

    log "restart phoenix agent"
    $agent_doc_base/startup.sh
    sleep 1
}
