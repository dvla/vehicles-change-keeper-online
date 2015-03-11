@tag
  Feature: Business keeper page filed validation

    Scenario:Fleet number - label
      Given that the user has selected Business on the vehicle lookup page
      When  the user is on the new-business-keeper-details page
      Then  there is a label titled "Fleet number"

    Scenario:- Fleet number - entry field
      Given that the user has selected Business on the vehicle lookup page
      When  the user is on the new-business-keeper-details page
      Then  there will be a data entry control for fleet number using the format NNNNNN or NNNNN-

    Scenario: - Fleet number - help text
      Given that the user has selected Business on the vehicle lookup page
      When  the user is on the new-business-keeper-details page
      Then  there will be help text displayed above the fleet number field "e.g. 012345 or 54321-"

    Scenario:- Fleet number - blank
      Given the fleet number is blank in business keeper details page
      When  the user clicks on the no email radio button
      When  the user select the submit control
      Then  the user can proceed without an error being displayed for the fleet number

    Scenario:- fleet number - valid format
      Given the fleet number is not blank and has a valid format in business keeper deatils page
      When  the user clicks on the no email radio button
      When  the user select the submit control
      Then  the user can proceed without an error being displayed for the fleet number

    Scenario: - fleet number - invalid format
      Given the fleet number has an invalid format in business keeper details page
      When  the user clicks on the no email radio button
      When  the user select the submit control
      Then  there is a fleet number error message displayed "Fleet number - The fleet number can only be a 6 digit number, please try again"

    Scenario: - Business name field characters length
      Given that the user has selected Business on the vehicle lookup page
      When  the user selects the field labelled Business name
      Then  the user can enter a business name of up to 30 characters

    Scenario: - Business name - invalid characters
      Given the business name contains invalid characters
      When  the user clicks on the no email radio button
      When  the user select the submit control
      Then  the user will receive an error message "Business name - Must be between 2 and 30 characters and only contain valid characters (a-z, A-Z, 0-9, &, -,(), /, ‘ and , or .). The following characters cannot be used at the start of business name (&,-,(), /, ‘ and , or .)"

    Scenario: - trimming
      Given the user has entered values into the business name
      When  the user clicks on the no email radio button
      When  the user select the submit control
      Then  invalid white space will be stripped from the start and end of the business name but spaces are allowed within the business name
      And   validation will be done on the entered text once the white space has been removed

    Scenario: - New keeper email - entry field
      Given that the user is on the Enter business keeper details page
      When  the user select the field labelled Email address
      Then  the user will be able to enter an email address of up to 254 characters

    Scenario: - New keeper email address is null
      Given that the user is on the Enter business keeper details page
      And   the user has not entered an email address
      When  the user select the submit control
      Then  the system will not display an error for missing or invalid email address

    Scenario: - Validate email address format
      Given that the user is on the Enter business keeper details page
      And   the user has entered an invalid email address
      When  the user select the submit control
      Then  the system will display an error for invalid email address "Contact email address - Enter a valid email address up to 254 characters"

    Scenario: - invalid postcode error message
      Given that the user is on the Enter business keeper details page
      When  the user tries to search on an invalid postcode
      Then  an error message  displays "Postcode - Must be between five and eight characters and in a valid format, e.g. AB1 2BA or AB12BA"
      And   the user does not progress to the next stage of the service

    Scenario: - blank postcode
      Given that the user is on the Enter business keeper details page
      When  the user tries to search on a blank postcode
      Then  an error message  displays "Postcode - Must be between five and eight characters and in a valid format, e.g. AB1 2BA or AB12BA"
      And   the user does not progress to the next stage of the service

    Scenario: - valid postcode
      Given that the user is on the Enter business keeper details page
      When  the user tries to search on a valid postcode
      Then  the user is presented with a list of matching addresses

    Scenario: - special characters in business name
      Given that the user is on the Enter business keeper details page
      When  the user enters special characters in businessname with valid data in rest of the fields
      Then  the user will sucessfully navigate to next page

    Scenario: - special characters in business name
      Given that the user is on the Enter business keeper details page
      When  the user enters special charcters at the start of the business name
      Then  the user will receive an error message "Business name - Must be between 2 and 30 characters and only contain valid characters (a-z, A-Z, 0-9, &, -,(), /, ‘ and , or .). The following characters cannot be used at the start of business name (&,-,(), /, ‘ and , or .)"
      And   will remain in the same page instead of progress to next page

