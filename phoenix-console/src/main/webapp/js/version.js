$(function(){
	
	bind_cmp_evt_handlers();
	
	
});

var confirm_timeout_id;

function bind_cmp_evt_handlers() {
	$(".version_row").hover(
		function() {
			$(this).find(".btn_container").show();
		},
		function() {
			$(this).find(".btn_container").hide();
		}
	);
	
	$("[name='btn_del']").click(function() {
		clearTimeout(confirm_timeout_id);
		$("#del_version").val($(this).attr("version"));
		$("#del_confirm").fadeIn('slow', function() {
			$this = $(this);
			confirm_timeout_id = setTimeout(function() {
				$this.fadeOut('slow');
			}, 3000);
		});
	});
	
	$("#del_cancel").click(function() {
		$("#del_confirm").hide();
		return false;
	});
	
	$("#del_confirm").click(function() {
		var removeuri = "/console/version?op=remove&id=" + $("#del_version").val();
		$(location).attr("href", removeuri.prependcontext());
		return false;
	});
	
	$("#create_btn").click(function() {
		var version = $("#version").val();
		$("#error_msg").text("");
		if (version.isBlank()) {
			$("#error_msg").text("Version必填!");
			return false;
		}
	});
	
}