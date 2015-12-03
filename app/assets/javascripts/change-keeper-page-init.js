// Define the dependency to page-init in common
define(['jquery', "page-init"], function($, pageInit) {

    var enableSendingGaEventsOnSubmit = function() {
        $('button[type="submit"]').on('click', function(e) {

            // K2K-001: tracking the optional email fields on the details of vehicle being sold page
            trackingOptionalRadioField("#vehicleSellerEmailOption", "seller_email");
            // K2K-002: tracking the optional email on the private keeper details page
            trackingOptionalRadioField("#privatekeeper_option_email", "private_keeper_email");
            // K2K-003: tracking the optional email on the business keeper details page
            trackingOptionalRadioField("#businesskeeper_option_email", "business_keeper_email");
            // K2K-004: tracking the optional fleet number on the business keeper details page
            trackingOptionalRadioField("#fleetNumberOption", "fleet_number");

            //K2K-006: tracking driving licence number on the private keeper details page
            trackingOptionalFields("#privatekeeper_drivernumber", "driving_licence");
            //K2K-007: tracking the vehicle mileage on the sales details page
            trackingOptionalFields("#mileage", "mileage");

            //K2K-005: tracking date of birth on the private keeper details page
            trackingDateFields("#privatekeeper_dateofbirth", "date_of_birth");

            //K2K-010: tracking if the new owner is a business or an individual
            trackPrivateBusiness();

        });
    };

    var addGaEventToV5Chint = function() {
        //Tracking event for click on V5C image tooltip
        if ($('.hint-image-wrap p').length) {
            var v5cHint = $('.hint-image-wrap p');
            v5cHint.on('click', function() {
                var currentEvent = $('.hint-image-wrap p').attr('data-tracking');
                _gaq.push(['_trackEvent', 'ct_link',  currentEvent, 'click',  1]);
            });
        }
    };

    var trackPrivateBusiness = function() {
        if ($("#vehicleSoldTo_Private").is(':checked')) {
            _gaq.push(['_trackEvent', "track_path", "individual"]);
        }
        if ($("#vehicleSoldTo_Business").is(':checked')) {
            _gaq.push(['_trackEvent', "track_path", "business"]);
        }

    };

    // tracks an event based on the state of an optional radio box. This will work for the yes/no radio boxes.
    // you need to pass the fieldSelector (for id: #id, for class: .className) and the name of the action.
    // value is optional
    var trackingOptionalRadioField = function(fieldSelector, actionName, value) {
        var visibleField = $(fieldSelector + "_visible");
        var invisibleField = $(fieldSelector + "_invisible");

        if (value === undefined) value = 1;

        if (visibleField.is(':checked')) {
            _gaq.push(['_trackEvent', "optional_field", actionName, 'provided', value]);
        }
        if (invisibleField.is(':checked')) {
            _gaq.push(['_trackEvent',  "optional_field", actionName, 'absent', value]);
        }
    };

    // tracks an event based on a field that has a value. e.g. a textfield.
    var trackingOptionalFields = function(fieldSelector, actionName, value) {
        if (value === undefined) value = 1;

        if($(fieldSelector).value == "") {
            _gaq.push(['_trackEvent', "optional_field", actionName, 'absent', value]);
        } else {
            _gaq.push(['_trackEvent', "optional_field", actionName, 'provided', value]);
        }
    };

    var trackingDateFields = function(fieldSelector, actionName, value) {

        if (value === undefined) value = 1;

        var field_day = $(fieldSelector + "_day");
        var field_month = $(fieldSelector + "_month");
        var field_year = $(fieldSelector + "_year");


        if(field_day.value == "" || field_month.value == "" || field_year.value == "") {
            _gaq.push(['_trackEvent', "optional_field", actionName, 'absent', value]);
        } else {
            _gaq.push(['_trackEvent', "optional_field", actionName, 'provided', value]);
        }
    };

    return {
        init: function() {
            // Call initAll on the pageInit object to run all the common js in vehicles-presentation-common
            pageInit.initAll();
            // Run the common code that is not common to all but this one needs
            pageInit.hideEmailOnOther('#privatekeeper_title_titleOption_4', '.form-item #privatekeeper_title_titleText');

            enableSendingGaEventsOnSubmit();
            addGaEventToV5Chint();
        }
    }
});
