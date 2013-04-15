#!/bin/bash

set -e
set -u

cd `dirname $0`
source ./util.sh

eval "`parse_argument_and_set_variable agent_git_url agent_version tx_log_file agent_doc_base agent_git_host func tmp_script_file agent_class agent_dryrun_class dry_run_port parent_pid`"

ensure_not_empty agent_git_url="$agent_git_url" agent_version="$agent_version" tx_log_file="$tx_log_file" agent_doc_base="$agent_doc_base" agent_git_host="$agent_git_host" func="$func" tmp_script_file="$tmp_script_file" agent_class="$agent_class" agent_dryrun_class="$agent_dryrun_class" dry_run_port="$dry_run_port" parent_pid="$parent_pid"

if [ $func = "init" ]; then
    log "PID is $$"
    log "CMD is $0 --func=init"

    tmp_upgrade_script=`mktemp /tmp/"$tmp_script_file"`
    cat self_upgrade_func.sh >> $tmp_upgrade_script
    cat self_upgrade_do.sh >> $tmp_upgrade_script
    chmod +x $tmp_upgrade_script
else
    tmp_upgrade_script="/tmp/$tmp_script_file"
    if [ -e $tmp_upgrade_script ]; then
    	echo $@
        $tmp_upgrade_script --agent_git_url="$agent_git_url" --agent_version="$agent_version" --tx_log_file="$tx_log_file" --agent_doc_base="$agent_doc_base" --agent_git_host="$agent_git_host" --func="$func" --tmp_script_file="$tmp_script_file" --agent_class="$agent_class" --agent_dryrun_class="$agent_dryrun_class" --dry_run_port="$dry_run_port" --parent_pid="$parent_pid"
    else
        exit 1
    fi
fi
