@tag
Feature: Minimum Happy and Sad Path Acceptance Tests for Keeper to Keeper

  Background:
    Given the user is on the vehicle look up page

  Scenario:Private Keeper Happy Path
    When the keeper sold the vehicle to the private keeper after entering valid registration and doc ref number and click on submit button
    And  the user is on Private Keeper details page and entered through successful postcode lookup
    Then the user will be on complete and confirm page and click on confirm sale button
    And  the user will be taken to private keeper succesful summary page

  Scenario:Business Keeper Happy Path
    When the keeper sold the vehicle to the Business keeper after entering valid registration and doc ref number and click on submit button
    And  the user is on Business Keeper details page and entered through successful postcode lookup
    Then the user will be on Business keeper complete and confirm page and click on confirm sale button
    Then the user will be taken to Business keeper succesful summary page

  Scenario:Business Keeper Happy Path with unsuccessful postcode
    When the keeper sold the vehicle to the Business keeper after entering valid registration and doc ref number and click on submit button
    And  the trader entered through unsuccessful postcode lookup business user
    Then the user will be on unsuccesful postcode Business keeper complete and confirm page and click on confirm sale button
    Then the user will be taken to  unsuccesful postcode Business keeper succesful summary page

  Scenario:Private Keeper Happy Path with unsuccessful postcode
    When the keeper sold the vehicle to the private keeper after entering valid registration and doc ref number and click on submit button
    And  the trader entered through unsuccessful postcode lookup private keeper
    Then the user will be on unsuccesful postcode Private keeper complete and confirm page and click on confirm sale button
    And  the user will be taken to Unsuccesful postcode private keeper details page succesful summary page
