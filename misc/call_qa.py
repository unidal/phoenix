#!/usr/bin/python

import sys
import httplib
import json

host = "127.0.0.1"
port = 8378
submit_path = "/test/service/task?op=submitTask"
query_path = "/test/service/task/"

conn = httplib.HTTPConnection(host, port)

def request_to_json(path):
	conn.request("GET", path)
	res = conn.getresponse()
	return json.loads(res.read())

#print sys.argv

res_obj = request_to_json(submit_path)
token = res_obj["token"]

exit_status = "DONE"
while True:
	res_obj = request_to_json(query_path + token)
	status = res_obj["status"]
	if(status == "DONE"):
		break
	elif(status == "ABORTED"):
		status = "ARORTED"
		break

print exit_status
