@tag
Feature:complete and confirm page field validation

  Background:
    Given that the user is on the date of sale page

  Scenario: mileage label
    Given   there is a  label titled "Vehicle mileage"
    Then    there is a control for entry of the vehicle mileage using the format N(6)

  Scenario: Date of Sale - label
    When  there is a labelled Date of Sale and hint text
    And   the Date of sale section will contain the Month label Month entry control Year label Year entry control

  Scenario: Date of Sale - Day
    When  the user selects the data entry control labelled Day
    Then  the user can enter the 1 or 2 digit day of the month
    And   the field will only accept the values 0-9

  Scenario: - Date of Sale - Month
    When  the user selects the data entry control labelled Month
    Then  the user can enter the 1 or 2 digit month of the year
    And   the field will only accept the values 0-9

  Scenario: - Date of Sale - Year
    When  the user selects the data entry control labelled Year
    Then  the user can enter the 4 digit year
    And   the field will only accept the values 0-9

  Scenario: Date of Sale - Past
    When  the Date of sale is in the past
    And   the user click on the next button
    Then  an error message displayed "Date of sale - We cannot accept a date of sale more than 5 years in the past. Please check and enter the correct date. If the date is correct then please submit the transaction via post."

  Scenario: Date of Sale - Future
    When  the Date of sale is in the future
    And   the user click on the next button
    Then  an error message displayed "Date of sale - Must be a valid date DD MM YYYY and not be in the future."

  Scenario: Date of Sale - Incomplete
    When  the Date of sale is incomplete
    And   the user click on the next button
    Then  an error message displayed "Date of sale - Must be a valid date DD MM YYYY and not be in the future."

  Scenario: Date of Sale - Invalid date
    When  the Date of sale is not a valid gregorian date
    And   the user click on the next button
    Then  an error message displayed "Date of sale - Must be a valid date DD MM YYYY and not be in the future."
