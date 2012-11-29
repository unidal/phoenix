$(function() {
    bind_cmp_evt_handlers();
});

function bind_cmp_evt_handlers() {
    $("#all-machine-check").click(function() {
        $("input[name='host']").attr("checked", $(this).is(":checked"));
    });
}