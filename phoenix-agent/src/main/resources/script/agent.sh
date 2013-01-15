set -e
set -u
#trap "echo 'INT signal received'" INT
#trap "echo 'TERM signal received'" TERM
cd `dirname $0`
source ./util.sh
source ./agent_func.sh

log "PID is $$"
log "CMD is $0 $@"

KERNEL_WAR_TMP=/data/webapps/phoenix/phoenix_kernel_war_tmp/
while getopts "h:g:e:k:b:x:c:d:v:f:" option;do
	case $option in
			h)      kernel_git_host=$OPTARG;;
			g)      kernel_git_url=$OPTARG;;
			e)      env=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
			k)      domain_kernel_base=$OPTARG;;
			b)      container_install_path=$OPTARG;;
			c)		container_type=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
			d)      domain=$OPTARG;;
			v)      kernel_version=$OPTARG;;
			f)      func=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
			x)      server_xml=$OPTARG;;
	esac
done

ensure_not_empty domain="$domain" version="$kernel_version" container_install_path="$container_install_path"
ensure_not_empty container_type="$container_type" server_xml="$server_xml" domain_kernel_base="$domain_kernel_base"
ensure_not_empty env="$env" kernel_git_url="$kernel_git_url" kernel_git_host="$kernel_git_host"

$func $@

