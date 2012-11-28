$(document).ready(
    function() {
        $.ajax("", {
            data : $.param({
                "op" : "status",
                "id" : $("#deploy_id").val(),
                "hosts" : ""
            }, true)
        });
        $.ajax({
            type : "GET",
            url : "?op=status",
            dataType : "json",
            data : {
                offset : $("#offset").val(),
                plan : $("#plan").val()
            },
            error : function(xhRequest, ErrorText,
                    thrownError) {
                console.log(xhRequest);
                console.log(ErrorText);
                console.log(thrownError);

                showResult("error", ErrorText);
                $(".progress").removeClass("active");
                stopTimer();
            },
            success : function(data) {

                var finished = data.status
                $
                        .each(
                                data.hosts,
                                function(hostIndex,
                                        host) {
                                    $("#p_" + hostIndex)
                                            .addClass(
                                                    "active");
                                    $
                                            .each(
                                                    host.status,
                                                    function(
                                                            statusIndex,
                                                            status) {
                                                        console.log(status);
                                                        $(
                                                                "#b_"
                                                                        + hostIndex
                                                                        + "_"
                                                                        + statusIndex)
                                                                .attr(
                                                                        "class",
                                                                        "bar bar-"
                                                                                + status);
                                                        $(
                                                                "#b_"
                                                                        + hostIndex
                                                                        + "_"
                                                                        + statusIndex)
                                                                .attr(
                                                                        "title",
                                                                        status);
                                                        if (statusIndex == host.status.length - 1
                                                                && (status == 'success'
                                                                        || status == 'warning' || status == 'failed')) {
                                                            $(
                                                                    "#p_"
                                                                            + hostIndex)
                                                                    .removeClass(
                                                                            "active");
                                                            $(
                                                                    "#p_"
                                                                            + hostIndex)
                                                                    .removeClass(
                                                                            "progress-striped");
                                                        }
                                                    });
                                });

                if (finished=="success") {
                    showResult("success",
                            "Deploy Completed");
                    $(".progress")
                            .removeClass("active");
                    stopTimer();
                }else if(finished=="failed"){
                    showResult("error","Deploy Failed");
                    $(".progress")
                    .removeClass("active");
                    stopTimer();
                }

                $("#offset").val(data.offset);

                if (data.content) {
                    $("#status")
                            .append(
                                    "<span id=offset-"
                                            + data.offset
                                            + " class=\"terminal-like\">"
                                            + data.content
                                            + "</span>");
                    $("#status").scrollTop($("#status").get(0).scrollHeight)
                }
            }
        });
    });

function fetch_deploy_status() {
    var hostArr = [];
    $(".host_status").map(function() {
        hostArr.push($(this).attr("data-host") + ":" + $(this).attr("data-offset"));
    });
    if(hostArr.length > 0) {
        $.ajax("", {
            data : $.param({
                "op" : "status",
                "id" : $("#deploy_id").val(),
                "hosts" : hostArr.join(",")
            }, true),
            dataType: "json",
            success: function(result) {

            },
            complete: function() {
                if (!is_deploy_finished()) {
                     setTimeout(fetch_deploy_status(), 1000);
                }
            }
        });
    }
}

function is_deploy_finished() {
    return $("#deploy_status").text() != "doing";
}

//var interval = setInterval('updateDeployStatus()', 1000);
//function stopTimer() {
//	clearInterval(interval);
//}

//function showResult(type, content) {
//	$("#result").html(
//			"<div id='alert' class='alert alert-" + type + "'>" + content
//					+ "</div>");
//	$("#result").show();
//}
