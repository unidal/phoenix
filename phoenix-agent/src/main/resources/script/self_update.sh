
set -e 
set -u

cd `dirname $0`
source ./util.sh

AGENT_WAR_TMP=/data/webapps/phoenix/phoenix_agent_war_tmp/
AGENT_DIR=/data/webapps/phoenix/phoenix-agent

agent_git_url=/Users/marsqing/Projects/tmp/tmp/repo
agent_version=v2
while getopts "g:v:" option;do
	case $option in
			g)      agent_git_url=$OPTARG;;
			v)      agent_version=$OPTARG;;
	esac
done

tmp_update_script=`mktemp /tmp/phoenix-agent-self-update.sh.XXXXXX`
cat util.sh >> $tmp_update_script
cat agent_func2.sh >> $tmp_update_script
cat update_agent.sh >> $tmp_update_script
chmod +x $tmp_update_script
$tmp_update_script -g $agent_git_url -v $agent_version -t $AGENT_WAR_TMP -a $AGENT_DIR &
