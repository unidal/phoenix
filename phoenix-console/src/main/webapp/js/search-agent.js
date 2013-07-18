$(".agent-opt .btn").click(function() {
	var target = $(this).parent().attr("for");
	$("#" + target).val($(this).text());
});
