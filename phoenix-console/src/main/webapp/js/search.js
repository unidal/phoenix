var focusId = 1;
var queryCount = 1;
var maxQueryCount = 3;
var dependencies = [];

function loadDependencies() {
	$(".ili").each(function() {
		dependencies.push($(this).text());
	});
}

function getNewQueryHtml() {
	var ret = "";
	var queryIdx = queryCount + 1;

	ret += '<div id="query' + queryIdx
			+ '" style="margin:10px 10px 0px 10px;">';
	ret += '<input id="join' + queryIdx + '" name="join' + queryIdx
			+ '" type="hidden" value="or">';
	ret += '<div class="btn-group jar-logic" data-toggle="buttons-radio" for="join'
			+ queryIdx + '">';
	ret += '<button type="button" class="btn">&amp;</button>';
	ret += '<button type="button" class="btn active">| |</button>';
	ret += '</div>&emsp;';
	ret += '<input id="dep'
			+ queryIdx
			+ '" name="dep'
			+ queryIdx
			+ '" class="dependency" type="text" placeholder="Type or select artifactId" data-provide="typeahead">';
	ret += '<input id="op' + queryIdx + '" name="op' + queryIdx
			+ '" type="hidden" value="=">&emsp;';
	ret += '<div class="btn-group jar-opt" data-toggle="buttons-radio" for="op'
			+ queryIdx + '">';
	ret += '<button type="button" class="btn">&lt;</button>';
	ret += '<button type="button" class="btn active">=</button>';
	ret += '<button type="button" class="btn">&gt;</button>';
	ret += '</div>&emsp;';
	ret += '<input id="ver' + queryIdx + '" name="ver' + queryIdx
			+ '" type="text" placeholder="Input version info">';
	ret += '</div>';

	return ret;
}

$("#qadd").click(function() {
	if (queryCount < maxQueryCount) {
		$("#queryform").append(getNewQueryHtml());
		queryCount++;
		focusId = queryCount;
		renewDom();
	} else {
		queryCount = maxQueryCount;
	}
});

$("#qdel").click(function() {
	if (queryCount > 1) {
		$("#query" + queryCount).remove();
		queryCount--;
		if (focusId > queryCount) {
			focusId--;
		}
		renewDom();
	} else {
		queryCount = 1;
	}
});

function renewDom() {
	$(".dependency").focus(function() {
		focusId = this.id.substring(this.id.length - 1);
	});
	$(".dependency").typeahead({
		source : dependencies
	});

	$(".jar-opt .btn").click(function() {
		var target = $(this).parent().attr("for");
		$("#" + target).val($(this).text());
	});

	$(".jar-logic .btn").click(function() {
		var target = $(this).parent().attr("for");
		$("#" + target).attr("value", $(this).text() == "&" ? "and" : "or");
	});
}

$(document).ready(function(e) {
	$(".btn-list").click(function() {
		$("#dep" + focusId).val($(this).text());
	});
	renewDom();
	loadDependencies();
});