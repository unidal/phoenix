var continuous_err_times = 0;
var MAX_CON_ERR_TIMES = 10;
$(function() {
    if (!is_deploy_finished()) {
        setTimeout(fetch_deploy_status, 1000);
    }
    bind_cmp_evt_handlers();
});

function bind_cmp_evt_handlers() {
    $(".host_status").click(function() {
        var host = $(this).attr("id");
        $(".log-arrow").addClass("hide");
        $(this).find(".log-arrow").removeClass("hide");
        $(".terminal").hide();
        $("#log-" + host.replace(/\./g, "\\.")).show();
    });
}

function fetch_deploy_status() {
    var hostArr = [];
    $(".host_status").map(function() {
        hostArr.push($(this).attr("id") + ":" + $(this).attr("data-offset"));
    });
    if(hostArr.length > 0) {
        $.ajax("", {
            data : $.param({
                "op" : "status",
                "id" : $("#deploy_id").val(),
                "hosts" : hostArr.join(",")
            }, true),
            dataType: "json",
            cache: false,
            success: function(result) {
                continuous_err_times = 0;
                if (result != null) {
                    $.each(result.hosts, function(index, obj) {
                        var host = obj.host.replace(/\./g, "\\.");
                        update_host_status(host, obj);
                        update_host_log(host, obj);
                    });
                    update_deploy_status(result.status);
                }
            },
            error: function(xhr, errstat, err) {
                continuous_err_times++;
            },
            complete: function() {
                if (!is_deploy_finished() && continuous_err_times < MAX_CON_ERR_TIMES) {
                     setTimeout(fetch_deploy_status, 1000);
                }
            }
        });
    }
}

function is_deploy_finished() {
    return $("#deploy_status").text() != "doing";
}

function update_host_status(host, data) {
    var hostStatus = data.status;
    var $hostProgress = $("#" + host).find(".progress");
    var $hostBar = $("#" + host).find(".bar");
    var $hostStep = $("#" + host).find(".step");
    if ("pending" == hostStatus) {
        $hostBar.css("width", "0%");
        $hostStep.text("");
    } else {
        $hostBar.css("width", data.progress + "%");
        $hostStep.text(data.step);
    }
    $("#" + host).attr("data-offset", data.offset);
    $hostProgress.removeClass().addClass("progress");
    if ("success" == hostStatus) {
        $hostProgress.addClass("progress-success");
    } else if ("failed" == hostStatus) {
        $hostProgress.addClass("progress-danger");
    } else if ("doing" == hostStatus) {
        $hostProgress.addClass("progress-striped active");
    }
}

function update_host_log(host, data) {
    //TODO data.log是否已经做了换行到<br />的转换，或者是使用数组形式
    if (data.log != null && data.log != "") {
        var $logContainer = $("#log-" + host);
        $logContainer.append("<div class=\"terminal-like\">" + data.log + "</div>");
        $logContainer.scrollTop($logContainer.get(0).scrollHeight);
    }
}

function update_deploy_status(status) {
    $("#deploy_status").text(status);
}
