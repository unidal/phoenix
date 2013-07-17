var payloadStr = "";

function parsePayload() {
	var deparray = getArrayFromString($("#payload_dependencies").val());
	var oparray = getArrayFromString($("#payload_operators").val());
	var verarray = getArrayFromString($("#payload_versions").val());
	var jointarray = getArrayFromString($("#payload_joints").val());

	for ( var idx = 0; idx < deparray.length; idx++) {
		payloadStr += "&dependency=" + deparray[idx];
	}
	for ( var idx = 0; idx < oparray.length; idx++) {
		payloadStr += "&operator=" + oparray[idx];
	}
	for ( var idx = 0; idx < verarray.length; idx++) {
		payloadStr += "&version=" + verarray[idx];
	}
	for ( var idx = 0; idx < jointarray.length; idx++) {
		payloadStr += "&joint=" + jointarray[idx];
	}
}

function getArrayFromString(str) {
	return str.substring(1, str.length - 1).split(",");
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
