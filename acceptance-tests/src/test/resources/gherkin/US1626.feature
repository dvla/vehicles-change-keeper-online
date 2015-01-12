@tag
  Feature:Private Keeper Details Page Fields validation

  Scenario:Title - No entry
    Given that the user selects NO Title option
    When the user selects the Submit function
    Then the user will be errored "Please enter the title of the new keeper"

  Scenario: Title - Valid entry
    Given a valid entry  exists for the title control
    When the user selects the Submit function
    Then user will not see the error "Please enter the title of the new keeper"

  Scenario: Title - Other title null
    Given the user selected the title  "Other"
    And the other title text input field is ""
    When the user selects the Submit function
    Then an error message will be displayed "Other - This is a required field"

  Scenario: - First name - null entry
    Given the user enter null value in firstname textbox
    When the user selects the Submit function
    Then an error message  should display "Must contain between 1 and 25 characters from the following a-z, A-Z, 0-9 and .,- “’ and space"

  Scenario: - First name - valid entry
    Given the user has entered valid firstName
    When the user selects the Submit function
    Then no error message will be displayed "First name - Must contain between 1 and 25 characters from the following a-z, A-Z, 0-9 and .,- “’ and space"

  Scenario: - First name - invalid entry
    Given the user has entered invalid firstName
    When the user selects the Submit function
    Then error message will be display "First name - Must contain between 1 and 25 characters from the following a-z, A-Z, 0-9 and .,- “’ and space"

  Scenario: - Last name - null entry
    Given the user enter null value in lastname textbox
    When the user selects the Submit function
    Then an error message  should display "Must contain between 1 and 25 characters from the following a-z, A-Z, 0-9 and .,- “’ and space"

  Scenario: - Last name - valid entry
    Given the user has entered valid lastName
    When the user selects the Submit function
    Then no error message will be displayed "Last name - Must contain between 1 and 25 characters from the following a-z, A-Z, 0-9 and .,- “’ and space"