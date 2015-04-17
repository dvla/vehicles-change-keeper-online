// Define the dependency to page-init in common
define(['jquery', 'jquery-migrate', "page-init"], function($, jqueryMigrate, pageInit) {

    return {
        init: function() {
            // Call initAll on the pageInit object to run all the common js in vehicles-presentation-common
            pageInit.initAll();
            // Run the common code that is not common to all but this one needs
            pageInit.hideEmailOnOther('#privatekeeper_title_titleOption_4', '.form-item #privatekeeper_title_titleText');
            pageInit.imageHintToggles()
        }
    }
});
