#!/bin/bash - 
#===============================================================================
#
#          FILE: AgentUpgradeTaskProcessor.sh
# 
#         USAGE: ./AgentUpgradeTaskProcessor.sh
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: marsqing
#  ORGANIZATION: 
#       CREATED: 02/19/2013 23:27:42 CST
#      REVISION:  ---
#===============================================================================

set -e
set -u

cd `dirname $0`
source ./util.sh
source ./git_util.sh

log "PID is $$"
log "CMD is $0 $@"

AGENT_CLASS="com.dianping.phoenix.agent.PhoenixAgent"
AGENT_DRY_RUN_CLASS="com.dianping.phoenix.agent.PhoenixAgentDryRun"
DRY_RUN_PORT=3474

options=$(getopt -o x -l name:,age: -- "$@")
eval set -- $options
while [ $# -gt 0 ]
do
	case "$1" in
		--name)	name="$2";shift;;
		--age)	age="$2";shift;;
		(--) shift;break;;
		(-*) echo "$0: error - unrecognized option $1"1>&2;exit 1;;
		(*) break;;
	esac
	shift
done

function gitPull {
	add_ssh_private_key
	git_pull $agent_git_url $agent_version $agent_war_tmp
}

function dryrunAgent {
	kill_by_javaclass $AGENT_DRY_RUN_CLASS
	chmod +x $agent_war_tmp/startup_dryrun.sh
	$agent_war_tmp/startup_dryrun.sh $AGENT_DRY_RUN_CLASS $DRY_RUN_PORT
}

function upgradeAgent {
	if [ -f $tx_log_file ];then
		exec 1>>$tx_log_file 2>&1
	fi
	kill_by_javaclass $AGENT_CLASS
	git_sync $agent_war_tmp $agent_dir
	git_commit $agent_dir "agent upgraded to $agent_version"
	chmod +x $agent_dir/startup.sh
	$agent_dir/startup.sh
}

