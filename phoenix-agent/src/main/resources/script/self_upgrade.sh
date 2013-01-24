
set -e 
set -u

cd `dirname $0`
source ./util.sh

log "PID is $$"
log "CMD is $0 $@"

AGENT_WAR_TMP=/data/webapps/phoenix/phoenix_agent_war_tmp/
AGENT_DIR=/data/webapps/phoenix/phoenix-agent

while getopts "g:v:l:" option;do
	case $option in
			g)      agent_git_url=$OPTARG;;
			v)      agent_version=$OPTARG;;
			l)      tx_log_file=$OPTARG;;
	esac
done

tmp_upgrade_script=`mktemp /tmp/phoenix-agent-self-upgrade.sh.XXXXXX`
cat util.sh >> $tmp_upgrade_script
cat agent_func2.sh >> $tmp_upgrade_script
cat upgrade_agent.sh >> $tmp_upgrade_script
chmod +x $tmp_upgrade_script
$tmp_upgrade_script -g $agent_git_url -v $agent_version -l $tx_log_file -t $AGENT_WAR_TMP -a $AGENT_DIR &
