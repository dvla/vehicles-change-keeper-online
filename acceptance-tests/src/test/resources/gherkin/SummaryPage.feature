@tag
Feature:
  Scenario: Successful summary page validation
    Given the user is on the successful summary page
    Then   the user can see the Transaction Id Finish and Print button
    And   the user can see the Thank you message and vehicle details
    And   the keeper can see the keeper details
