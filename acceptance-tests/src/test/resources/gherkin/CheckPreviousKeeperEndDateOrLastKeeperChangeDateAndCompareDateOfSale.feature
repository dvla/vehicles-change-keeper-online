@tag
Feature:

  Scenario: Complete and confirm - Confirm new keeper with a date of sale before the previous keeper end date
    Given the user is on the Complete and Confirm page with Vehicle Registration number as "AA11AAL"
    When the user enters a date of sale before the previous keeper end date and click on submit button
    Then the user will remain on the complete and confirm page and a warning will be displayed
    And the user confirms the transaction
    And the user will be taken to the "Summary" page

  Scenario: Complete and confirm - Confirm new keeper with a date of sale before the last keeper change date
    Given the user is on the Complete and Confirm page with Vehicle Registration number as "AA11AAM"
    When the user enters a date of sale before the last keeper change date and click on submit button
    Then the user will remain on the complete and confirm page and a warning will be displayed
    And the user confirms the transaction
    And the user will be taken to the "Summary" page

  Scenario: Complete and Confirm - without keeper end date and change date returned by the back end system
    Given the user is on the Complete and confirm page with Vehicle Registration Number as "AA11AAJ"
    When no date is returned by the back end system and  user enters a date of sale
    Then the user will not see a warning message
    And the user will be taken to the "Summary" page

  Scenario: Complete and Confirm - both keeper end date and change date returned by the back end system
    Given the user is on the Complete and confirm page with Vehicle Registration Number as "AA11AAK"
    When keeper end date and change date has been returned by the back end system
    Then the user will not see a warning message
    And the user will be taken to the "Summary" page