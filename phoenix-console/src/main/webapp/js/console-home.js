var payloadStr = "";

function parsePayload() {
	var deparray = getArrayFromString($("#payload_dependencies").val());
	var oparray = getArrayFromString($("#payload_operators").val());
	var verarray = getArrayFromString($("#payload_versions").val());
	var jointarray = getArrayFromString($("#payload_joints").val());

	for ( var idx = 0; idx < deparray.length; idx++) {
		payloadStr += deparray[idx] == "" ? "" : "&dependency="
				.concat(deparray[idx]);
	}
	for ( var idx = 0; idx < oparray.length; idx++) {
		payloadStr += oparray[idx] == "" ? "" : "&operator="
				.concat(oparray[idx]);
	}
	for ( var idx = 0; idx < verarray.length; idx++) {
		payloadStr += verarray[idx] == "" ? "" : "&version="
				.concat(verarray[idx]);
	}
	for ( var idx = 0; idx < jointarray.length; idx++) {
		payloadStr += jointarray[idx] == "" ? "" : "&joint="
				.concat(jointarray[idx]);
	}

	var agentversion = $("#payload_agentversion").val();
	var agentoperator = $("#payload_agentoperator").val();
	payloadStr += agentversion == "" ? "" : "&agentversion="
			.concat(agentversion);
	payloadStr += agentoperator == "" ? "" : "&agentoperator="
			.concat(agentoperator);
}

function getArrayFromString(str) {
	return str.substring(1, str.length - 1).split(", ");
}

$(function() {
	parsePayload();
	$("a.toProject").attr("href", function() {
		return $(this).attr("href") + payloadStr;
	});
	$('#myTab a').click(function(e) {
		$(this).tab('show');
	})
});
