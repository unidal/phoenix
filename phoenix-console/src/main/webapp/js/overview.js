$("li").click(function() {
	$(this).parent().children().removeClass("active");
	$(this).addClass("active");

	var domain = $(this).text();
	$.ajax({
		async : false,
		url : "/console/home",
		data : {
			"op" : "domaininfo",
			"getdomain" : domain
		},
		dataType : 'json'
	}).done(function(data) {
		var tablestr = generateTable(data);
		$("#inner").html(tablestr);
		var otb = $("#detail").dataTable({
			"sScrollX" : "100%",
			"bAutoWidth": false
		});
		new FixedColumns(otb, {
			"iLeftColumns" : 2
		});
	});
});

function generateTable(data) {
	var table = "";

	var head = data[0];
	var content = data.slice(1);

	table += "<table id=\"detail\" class=\"table table-hover table-striped\">";
	table += "<thead><tr>";
	for ( var idx = 0; idx < head.length; idx++) {
		table += "<th>" + head[idx] + "</th>";
	}
	table += "</tr></thead><tbody>";

	for ( var i = 0; i < content.length; i++) {
		table += "<tr>";
		var row = content[i];
		for ( var idx = 0; idx < row.length; idx++) {
			table += "<td>" + row[idx] + "</td>";
		}
		table += "</tr>";
	}
	table += "</tbody></table>";

	return table;
}

$(function() {
	$("#accordion ul:first").children().first().click();
	$(".btn-accordion").click(function() {
		$(this).parent().parent().find(".active").click();
	});
});