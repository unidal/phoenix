set -e
set -u

cd `dirname $0`
source ./util.sh

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

ensure_not_empty git_url="$agent_git_url" version="$agent_version" log_file="$tx_log_file" doc_base="$agent_doc_base" git_host="$agent_git_host" func="$func" tmp_script_file="$tmp_script_file" agent_class="$agent_class" agent_dryrun_class="$agent_dryrun_class" dry_run_port="$dry_run_port" parent_pid="$parent_pid"

if [ $func = "init" ]; then
    log "PID is $$"
    log "CMD is $0 $@"

    tmp_upgrade_script=`mktemp /tmp/"$tmp_script_file"`
    cat self_upgrade_func.sh >> $tmp_upgrade_script
    cat self_upgrade_do.sh >> $tmp_upgrade_script
    chmod +x $tmp_upgrade_script
else
    tmp_upgrade_script="/tmp/$tmp_script_file"
    if [ -e $tmp_upgrade_script ]; then
        $tmp_upgrade_script $@
    else
        exit 1
    fi
fi