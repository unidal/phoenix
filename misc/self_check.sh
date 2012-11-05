function self_check_war {
	url_to_check="http://127.0.0.1:8080/inspect"
	log "Start testing web app..." i f f
	i=0
	biz_started="false"
	while [ $i -lt $MAX_HTTP_TRY ]; do 
		i=$((i+1))
		curl -I $url_to_check >/dev/null 2>&1
		if [ $? == 7 ]; then
			echo "attempt $i"
			sleep 1
		else
			biz_started="true"
			break
		fi
	done

	exit_code=0
	if [ $biz_started == "true" ]; then
		res_code=`curl -I $url_to_check 2>/dev/null | head -n1 | awk '{print $2}'`
		level="e"
		if [ x$res_code == "x200" ];then
			level="i"
		else
			exit_code=1
		fi
		log "HTTP response code is $res_code" $level t
	else
		exit_code=1
		log "$artifactId failed to start after $MAX_HTTP_TRY seconds" e t
	fi

	#log "Errors in catalina.out" w
	grep -E "ERROR|Error|SEVERE" $TOMCAT_HOME/logs/catalina.out
	if [ $forkGrep == "t" ];
	then
		log "Press Ctrl-C to exit"
		tail -f $TOMCAT_HOME/logs/catalina.out | grep -E "ERROR|Error|SEVERE"
	fi
	exit $exit_code
}
