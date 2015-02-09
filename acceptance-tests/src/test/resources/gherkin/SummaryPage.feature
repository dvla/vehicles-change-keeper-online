@tag
Feature:
  Scenario: Successful summary page validation
    Given the user is on the successful summary page
    And   the user can see the Transaction Id Finish and Print button
    And   the user can see the Thank you message and vehicle details
    And   the keeper can see the keeper details
    When  the user click on Finish button
    Then  the user can navigates to BeforeStartPage


