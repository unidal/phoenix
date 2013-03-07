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

# make git_dir a git repo if not one already and commit all files
# Parameter: 1. git_dir
function git_create_repo {
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

# git reset dest_dir and checkout tag of git_url to dest_dir
# Parameter: 1. git_url, 2. tag, 3. dest_dir
function git_pull {

	local git_url=$1
	shift
	local tag=$1
	shift
	local dest_dir=$1

	log "pull git repo at $git_url tag $tag to $dest_dir"
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

# make dest_dir a git repo and copy all files from src_dir to dest_dir and commit them
# essentially make files in dest_dir the same as src_dir and rollbackable to previous state
# Parameter: 1. src_dir, 2. dest_dir
function git_sync {

	local src_dir=$1
	shift
	local dest_dir=$1

	log "upgrading $dest_dir from $src_dir"

	if [ ! -e $dest_dir ];then
		mkdir -p $dest_dir
		# to avoid warning when git reset
		touch $dest_dir/dummy
	fi

	git_create_repo $dest_dir	

	rm -rf $dest_dir/*
	cp -r $src_dir/* $dest_dir/
	cd $dest_dir
	git add .
	cd - >/dev/null
	log "$dest_dir upgraded"
}

# Rollback a git directory
# Parameter: 1. git_directory
function git_rollback {
	local git_dir=$1
	cd $git_dir
	git reset --hard
	cd - >/dev/null
}

# Commit a git directory
# Parameter: 1. git_directory, 2. comment
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

