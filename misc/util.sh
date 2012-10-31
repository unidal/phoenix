
# argements:
#	message
#	level: i, w, e
#	output from line beginning: t, f
#	output tailing newline: t, f
function log {
	color="[32m"
	if [ x$2 == "xw" ];then
		color="[33m"
	elif [ x$2 == "xe" ];then
		color="[31m"
	fi

	if [ x$3 == "xt" ];then
		echo -en "\r\033[K"
	fi

	newline=""
	if [ x$4 == "xf" ];then
		newline=" -n "
	fi
	
	echo $newline $color$1[0m
}

function exit_on_error {
	if [ $? -ne 0 ];then
		if [ $# -ge 1 ];then
			log $1 e t
		fi
		exit 1
	fi
}
