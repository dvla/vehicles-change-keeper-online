@tag
Feature:
  Scenario: AC1 - No address selected
    Given the user has not selected an address on the Select new keeper address page
    When the user selects Next button
    Then the user is presented with an error message "Choose the new keepers address - Please select a valid address"

  Scenario: AC2 - Address selected and Next navigation
    Given the user has selected an address from the returned lookup on the Select new keeper address page
    When the user selects Next button
    Then the user is progressed to the next page

  Scenario: AC3 - Back Navigation
    Given the user is on the Select new keeper address page
    When the user selects Back button
    Then the user is progressed to the previous page

  Scenario: AC4 - Address not listed link Navigation
    Given the user is on the Select new keeper address page
    When the user selects Address not listed link
    Then the user is progressed to the Manual Address page