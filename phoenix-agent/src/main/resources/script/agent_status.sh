
set -e
set -u

cd `dirname $0`
source ./util.sh

while getopts "b:c:e:" option;do
	case $option in
			e)      env=$OPTARG;;
			b)      container_install_path=$OPTARG;;
			c)		container_type=`echo $OPTARG | tr '[A-Z]' '[a-z]'`;;
	esac
done

ensure_not_empty container_install_path="$container_install_path" container_type="$container_type" env="$env"

./op_${env}.sh -o container_status -b $container_install_path -c $container_type

