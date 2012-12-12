
function log {
	echo "[`date +'%m-%d %H:%M:%S'`][INFO] $@"
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
