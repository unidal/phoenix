cd `dirname $0`
#echo "running egret with $1 $2"

TOMCAT_HOME="/Users/marsqing/Projects/tmp/tomcat/"
LIB_REPO_URL="ssh://git@192.168.8.22:58422/egretlib"

if [ x$TOMCAT_HOME = "x" ]
then
	log "set TOMCAT_HOME first" "ERROR"
	exit 1
fi

LIB_DIR=`pwd`/egret-lib/
TOMCAT_DIR=$TOMCAT_HOME
WEBAPP_DIR="$TOMCAT_DIR/webapps/egret-demo-1.0.0-SNAPSHOT/"

function log {
	level="INFO"
	if [ ! x$2 = "x" ]
	then
		level=$2
	fi
	date=`date "+%Y-%m-%d %H:%M:%S"`
	sleep 0.5
	echo "[$date] [$level] $1"
}

function prepare {
	log "Start prepare..."
	rm -rf $LIB_DIR
	log "Fetch app update version $1 from repository..."
	git clone $LIB_REPO_URL $LIB_DIR -b $1
	log "Done"
	#check if version compatible
	mkdir -p $WEBAPP_DIR
	if [ ! -d "$WEBAPP_DIR/.git" ]
	then
		cd $WEBAPP_DIR
		git init > /dev/null
		git add * > /dev/null
		git commit -m "`date`" > /dev/null
	fi
	log "All done!"
	exit 0
}

function activate {
	log "Start activate..."
	if [ ! -d $WEBAPP_DIR/WEB-INF/lib ]
	then
		mkdir -p $WEBAPP_DIR/WEB-INF/lib/
	fi
	log "Stopping web server..."
	pid=`jps -l|grep Bootstrap|awk '{print $1}'`
	kill -9 $pid
	log "Done"
	#cp $LIB_DIR/*.jar $WEBAPP_DIR/WEB-INF/lib/
	log "Replace files..."
	for jar in `ls $LIB_DIR/*.jar`
	do
		log "Updating `basename $jar`"
		cp $jar $WEBAPP_DIR/WEB-INF/lib/
	done
	log "Done"
	cd $WEBAPP_DIR
	git add *
	log "Starting web server..."
	bash $TOMCAT_DIR/bin/startup.sh
	log "Done"
	log "All done!"
	exit 0
}

function commit {
	log "Start commit..."
	log "Commit updated files..."
	cd $WEBAPP_DIR
	git add *
	git commit -m "`date`"
	log "Done"
	log "Clean temp directories..."
	rm -rf $LIB_DIR
	#return [webapp.commit]
	log "Done"
	log "All done!"
	exit 0
}

function rollback {
	log "Start rollback..."
	log "Rollback updated files..."
	cd $WEBAPP_DIR
	git reset --hard
	log "Done"
	log "Clean temp directories..."
	rm -rf $LIB_DIR/*
	log "Done"
	log "Stopping web server..."
	pid=`jps -l|grep Bootstrap|awk '{print $1}'`
	kill -9 $pid
	log "Done"
	log "Starting web server..."
	bash $TOMCAT_DIR/bin/startup.sh
	log "Done"
	log "All done!"
	exit 0
}

if [ $1 = "prepare" ]
then
	prepare $2
elif [ $1 = "activate" ]
then
	activate
elif [ $1 = "commit" ]
then
	commit
elif [ $1 = "rollback" ]
then
	rollback
fi
exit 111



#bar=""
#for((i=10;i<=100;i+=10))
#do
#	bar=$bar"="
#	echo $bar $i%
#	sleep 1
#done
