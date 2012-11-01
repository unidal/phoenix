
# argements:
#	message
#	level: i, w, e
#	output from line beginning: t, f
#	output tailing newline: t, f
function log {
	color="[INFO] "
	if [ x$2 == "xw" ];then
		color="[WARN] "
	elif [ x$2 == "xe" ];then
		color="[ERROR]"
	fi
	
	echo $color$1
}

function exit_on_error {
	if [ $? -ne 0 ];then
		if [ $# -ge 1 ];then
			log $1 e t
		fi
		exit 1
	fi
}
