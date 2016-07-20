@tag
Feature:

  Scenario: Date of sale - Confirm new keeper with a date of sale before the previous keeper end date
    Given The user goes to the Date of sale page entering registration number: AA11AAL
    When the user enters a date of sale before the previous keeper end date and click on submit button
    Then the user will remain on the Date of Sale page and a warning will be displayed "Our records show you became the keeper of this vehicle"
    And the user confirms the date
    Then the user will be taken to the "Summary" page

  Scenario: Date of sale - Confirm new keeper with a date of sale before the last keeper change date
    Given The user goes to the Date of sale page entering registration number: AA11AAM
    When the user enters a date of sale before the last keeper change date and click on submit button
    Then the user will remain on the Date of Sale page and a warning will be displayed "Our records show you became the keeper of this vehicle"
    And the user confirms the date
    Then the user will be taken to the "Date of sale" page

  Scenario: Date of sale - Confirm date of sale over 12 months
    Given The user goes to the Date of sale page entering registration number: AA11AAR
    When the user enters a date of sale that is over 12 months in the past and click on submit button
    Then the user will remain on the Date of Sale page and a warning will be displayed "The date you have entered is over 12 months ago, please check the date to make sure it is correct."
    And the user confirms the date
    Then the user will be taken to the "Date of sale" page

  Scenario: Date of sale - without keeper end date and change date returned by the back end system
    Given The user goes to the Date of sale page entering registration number: AA11AAJ
    When no date is returned by the back end system and user enters a date of sale
    Then the user will not see a warning message
    And the user will be taken to the "Date of sale" page

  Scenario: Date of sale - both keeper end date and change date returned by the back end system
    Given The user goes to the Date of sale page entering registration number: AA11AAK
    When keeper end date and change date has been returned by the back end system
    Then the user will not see a warning message
    And the user will be taken to the "Date of sale" page
