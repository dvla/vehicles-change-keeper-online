@tag
  Feature:Private Keeper Details Page Fields validation and navigation to address selection page

  Background:
    Given the user is on the Private keeper details page

  Scenario: without  Private Keeper title selection
    When the user click on Submit button with out any title selection
    Then the user will remain on the same page with an error message "Please enter the title of the new keeper"

  Scenario: Private Keeper Other Title text box value as null
    When the user selects Other title radio button and then click on Submit button
    But  not entered any text in Other title text box
    Then the user will remain on the same page with an error message "Other - This is a required field"

  Scenario: Private Keeper First name - null entry
    When the user click on Submit button by not entering any text on FirstName textBox
    Then the user will remain on the same page with an error message "Must contain between 1 and 25 characters from the following A-Z, hyphen, apostrophe, full stop and space"

  Scenario: Private Keeper First name - invalid entry
    When the user click on Submit button with invalid text on FirstName textBox
    Then the user will remain on the same page with an error message "First name - Must contain between 1 and 25 characters from the following A-Z, hyphen, apostrophe, full stop and space"

  Scenario: Private Keeper Last name - invalid entry
    When the user click on Submit button with invalid text on LastName textBox
    Then the user will remain on the same page with an error message "Last name - Must contain between 1 and 25 characters from the following A-Z, hyphen, apostrophe, full stop and space"

  Scenario: Private Keeper Last name - null entry
    When the user click on Submit button by not entering any text on LastName textBox
    Then the user will remain on the same page with an error message "Must contain between 1 and 25 characters from the following A-Z, hyphen, apostrophe, full stop and space"

  Scenario: Private Keeper Navigation to Address selection page with no validation errors
    When the user navigates forwards from PrivateKeeper details page and there are no validation errors
    Then the user is taken to the page entitled "Select new keeper address"

  Scenario: Private Keeper Back Navigation to Vehicle Look Page
    When the user click on Back link Text
    Then the user Navigates back from the Private Keeper details page to Vehicle Lookup Page

  Scenario: Private Keeper Title and FirstName length should equal or less than 27 characters
    When the user select any title radio button
    And  the total number of characters is equal or less than "27" including title, space and FirstName
    And  click on submit button without any validation errors
    Then the user is taken to the page entitled "Select new keeper address"

  Scenario: Private Keeper Title and FirstName length more than 27 characters
    When the user select any title radio button
    And  the total number of characters is more than "27" including title, space and FirstName
    And  click on submit button without any validation errors
    Then the user will remain on the same page with an error message "Enter new keeper details"
