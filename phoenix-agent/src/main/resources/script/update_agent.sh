
set -e
set -u

AGENT_CLASS="com.dianping.phoenix.agent.PhoenixAgent"
AGENT_DRY_RUN_CLASS="com.dianping.phoenix.agent.PhoenixAgentDryRun"
DRY_RUN_PORT=3474

function exit_hook {
	echo "exit hook"
}
trap exit_hook EXIT

while getopts "g:v:t:a:" option;do
	case $option in
			g)      agent_git_url=$OPTARG;;
			v)      agent_version=$OPTARG;;
			t)      agent_war_tmp=$OPTARG;;
			a)      agent_dir=$OPTARG;;
	esac
done

sleep 1
create_git_repo $agent_dir
sync_git_repo $agent_git_url $agent_version $agent_war_tmp
kill_by_javaclass $AGENT_DRY_RUN_CLASS
new_agent_ok=true
chmod +x $agent_war_tmp/startup.sh
$agent_war_tmp/startup.sh $AGENT_DRY_RUN_CLASS $DRY_RUN_PORT || { new_agent_ok=false; }
if [ $new_agent_ok == true ];then
	log "new agent is valid, replace current agent"
	kill_by_javaclass $AGENT_CLASS
	upgrade_local_git_repo $agent_war_tmp $agent_dir
	$agent_dir/startup.sh
else
	log_error "new agent is corrputed, won't update"	
fi
