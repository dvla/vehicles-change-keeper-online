require(['config'], function() {
    require(["change-keeper-page-init"], function(changeKeeperPageInit) {
        $(function() {
            changeKeeperPageInit.init();
        });
    });
});
