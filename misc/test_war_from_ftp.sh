cd `dirname $0`
ftp_root="ftp://10.1.1.81/"
ftp_tmp="target/ftp/"

#rm -rf $ftp_tmp
mkdir -p $ftp_tmp

# convert 2012-11-12_11:00:00 to timestamp
function mydate_to_second {
	second=""
	if [ uname == "Linux" ];then	
		normalized_date=`echo $1 | awk -F "-|_" '{printf("%d-%d-%d %d:%d:%d", $1, $2, $3, $4, $5, $6);}'`
		second=`date -d "$normalized_date" +"%s" 2>/dev/null`
	else
		second=`date -j -f "%Y-%m-%d_%H-%M-%S" "$1" "+%s" 2>/dev/null`
	fi
}

# args: whole_line column_idx(start from 1)
function extract_column {
	echo "`echo $1 | awk -v column_idx=$2 '{column=$column_idx; gsub(/[[:space:]]/, "", column);print column;}'`"
}

function get_directory_list {
	wget --no-remove-listing "$1/" >/dev/null 2>&1
	rm -f index.html*
}

function fetch_artifact {
	local artifactId=$1
	get_directory_list "$ftp_root/$artifactId"
	local max_date=-1
	local dir_to_download=""
	while read line;do
		date=$(extract_column "$line" 9)
		if [ x${date:0:3} == "x201" ];then
			mydate_to_second $date
			if [ $? -ne 0 ];then
				break
			fi
			if [ x$second != x ];then
				if [ $second -gt $max_date ];then
					max_date=$second
					dir_to_download=$date
				fi
			fi
		fi
	done < .listing
	if [ x$dir_to_download != x ];then
		get_directory_list "$ftp_root/$artifactId/$dir_to_download"
		while read line;do
			file_name=$(extract_column "$line" 9)
			if [ x${file_name: -4} == "x.war" ];then
				echo "$artifactId: $dir_to_download"
				if [ $artifactId == "product-alpaca" ];then
					war_to_test=$ftp_tmp/$file_name
					unzip -l $war_to_test >/dev/null 2>&1
					if [ $? -ne 0 ];then
						wget -O $war_to_test $ftp_root/$artifactId/$dir_to_download/$file_name
					fi
					zip -d $war_to_test WEB-INF/lib/lion-product-1.0.0.jar >/dev/null 2>&1
					zip -r $war_to_test WEB-INF/
					./integration_test.sh -w $war_to_test -c tomcat
					exit
				fi
			fi
		done < .listing
	fi
}

get_directory_list $ftp_root
i=0
while read line;do
	artifactId=$(extract_column "$line" 9)
	if [ ${artifactId:0:1} = "." ];then
		continue
	fi
	artifactIds[$i]=$artifactId
	i=$((i+1))
done < .listing

for((i=0;i<${#artifactIds[*]};i++));do
	fetch_artifact ${artifactIds[$i]}
done
#fetch_artifact "product-user-admin-server"
#mydate_to_second "2012-11-12_00-00-00"
#echo $second
