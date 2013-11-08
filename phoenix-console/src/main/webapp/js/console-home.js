var payloadStr = "";

function parsePayload() {
	var queryTips = "";

	var deparray = getArrayFromString($("#payload_dependencies").val());
	var oparray = getArrayFromString($("#payload_operators").val());
	var verarray = getArrayFromString($("#payload_versions").val());
	var jointarray = getArrayFromString($("#payload_joints").val());

	for ( var idx = 0; idx < deparray.length; idx++) {
		if (idx > 0) {
			payloadStr += "&joint=".concat(jointarray[idx - 1]);
			queryTips += " " + jointarray[idx - 1] + " ";
		}
		payloadStr += "&dependency=".concat(deparray[idx]);
		payloadStr += "&operator=".concat(oparray[idx]);
		payloadStr += "&version=".concat(verarray[idx]);
		queryTips += deparray[idx] + oparray[idx] + verarray[idx];
	}

	var agentversion = $("#payload_agentversion").val();
	var agentoperator = $("#payload_agentoperator").val();

	payloadStr += agentversion == "" ? "" : "&agentversion="
			.concat(agentversion);
	payloadStr += agentoperator == "" ? "" : "&agentoperator="
			.concat(agentoperator);
	if (agentversion != "" && agentoperator != "") {
		queryTips += "AgentVersion " + agentoperator + " " + agentversion;
	}

	if (queryTips != "") {
		$("#queryInfo").html(queryTips);
		$("#queryInfo").parent().css({
			"display" : ""
		});
	}
}

function getArrayFromString(str) {
	var sourceArray = str.substring(1, str.length - 1).split(", ");
	var finalArray = [];
	for ( var idx = 0; idx < sourceArray.length; idx++) {
		if (sourceArray[idx] != "") {
			finalArray.push(sourceArray[idx]);
		}
	}
	return finalArray;
}

$(function() {
	parsePayload();
	$("a.toProject").attr("href", function() {
		return $(this).attr("href") + payloadStr;
	});
	$('#myTab a').click(function(e) {
		e.preventDefault();
		$(this).tab('show');
	});
	$('table').dataTable({
		"bPaginate" : false
	});
});
