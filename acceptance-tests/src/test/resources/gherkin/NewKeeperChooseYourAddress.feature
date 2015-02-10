@tag
Feature:
  Background:
    Given the user is on the NewKeeper choose your address page

  Scenario: NewKeeper choose your address - Next
    When the user navigates forwards from NewKeeper choose your address page and there are no validation errors
    Then the user is taken to the page entitled "Complete and confirm"

  Scenario: NewKeeper choose your address - choose to enter address manually
    When the user navigates forwards from NewKeeper choose your address page to the enter address manually page
    Then the user is taken to the page entitled "Enter an address"

  Scenario: NewKeeper choose your address - Back
    When the user navigates backwards from the NewKeeper choose your address page
    Then the user is taken to the page entitled "Enter new business keeper details"

  Scenario: No address selected
    When the user has not selected an address on the Select new keeper address page and click on Next button
    Then the user is taken to the page entitled "Choose the new keepers address - Please select a valid address"