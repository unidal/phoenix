set -e
set -u

log "PID is $$"
log "CMD is $0 $@"

while getopts "g:v:l:a:h:f:t:c:d:p:i:" option;do
    case $option in
        g)  agent_git_url=$OPTARG;;
        v)  agent_version=$OPTARG;;
        l)  tx_log_file=$OPTARG;;
        a)  agent_doc_base=$OPTARG;;
        h)  agent_git_host=$OPTARG;;
        f)  func=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
        t)  tmp_script_file=$OPTARG;;
        c)  agent_class=$OPTARG;;
        d)  agent_dryrun_class=$OPTARG;;
        p)  dry_run_port=$OPTARG;;
        i)  parent_pid=$OPTARG;;
    esac
done

agent_war_tmp=/data/webapps/phoenix/phoenix_agent_war_tmp/

$func $@

