@tag
  Feature:complete and confirm page field validation

    Background:
      Given that the user is on the complete and confirm page

    Scenario: mileage lable
      Given   there is a  label titled "Vehicle mileage (optional)"
      Then    there is a control for entry of the vehicle mileage using the format N(6)

    Scenario: Date of Sale - lable
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

    Scenario: Date of Sale-validation feature date
      When  the Date of sale is in the future
      And   the user click on confirm sale button
      Then  an error message displayed "Date of sale cannot be in the future"

    Scenario: Date of Sale - Incomplete
      When  the Date of sale is incomplete
      And   the user click on confirm sale button
      Then  an error message displayed "Date of sale - Must be a valid date DD MM YYYY and not be in the future."

    Scenario: Date of Sale -invalid date
      When  the Date of sale is not a valid gregorian date
      And   the user click on confirm sale button
      Then  an error message displayed "Date of sale - Must be a valid date DD MM YYYY and not be in the future."

    Scenario:complete and confirm-consent checkbox unchecked
      When the consent field is not checked
      And   the user click on confirm sale button
      Then  an error message displayed "You must have the consent of the new keeper to notify DVLA of the purchase of this vehicle"
      And  the user is not progressed to the next page

    Scenario:complete and confirm-consent checkbox checked
      When the consent field is checked
      And   the user click on confirm sale button
      Then the user is progressed to the next stage of the service
