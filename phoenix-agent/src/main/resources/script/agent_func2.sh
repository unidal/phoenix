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
