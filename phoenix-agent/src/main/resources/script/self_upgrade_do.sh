set -e
set -u

log "PID is $$"
log "CMD is $0 $@"

eval "`parse_argument_and_set_variable agent_git_url agent_version tx_log_file agent_doc_base agent_git_host func tmp_script_file agent_class agent_dryrun_class dry_run_port parent_pid`"

agent_war_tmp=/data/webapps/phoenix/phoenix_agent_war_tmp/

$func