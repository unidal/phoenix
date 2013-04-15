#!/bin/bash
set -e
set -u
#trap "echo 'INT signal received'" INT
#trap "echo 'TERM signal received'" TERM
cd `dirname $0`
source ./util.sh
source ./agent_func.sh

log "PID is $$"
log "CMD is $0 $@"

eval "`parse_argument_and_set_variable kernel_git_host kernel_git_url env domain_kernel_base container_install_path container_type domain kernel_version func server_xml`"

KERNEL_WAR_TMP=/data/webapps/phoenix/phoenix_kernel_war_tmp/

ensure_not_empty domain="$domain" version="$kernel_version" container_install_path="$container_install_path"
ensure_not_empty container_type="$container_type" server_xml="$server_xml" domain_kernel_base="$domain_kernel_base"
ensure_not_empty env="$env" kernel_git_url="$kernel_git_url" kernel_git_host="$kernel_git_host"

$func $@
