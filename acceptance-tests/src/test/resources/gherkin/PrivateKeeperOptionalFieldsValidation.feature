@tag
  Feature:Private keeper optional fields validation

   Scenario: - Date of birth - Valid
     Given the user enters a validate date of birth
     When the user clicks on the private keeper no email radio button
     When the user press the submit control
     Then the user will not see any error message like "Please enter a valid date in the format DD MM YYYY "

   Scenario: - Date of birth - Invalid or incomplete
     Given the user enters a invalid date of birth  and no other errors persists
     When the user clicks on the private keeper no email radio button
     When the user press the submit control
     Then there will be an error message displayed "Must be a valid date DD MM YYYY and not be in the future."

   Scenario: - Date of birth - Future date
     Given the user enters the dateOfBirth in future
     When the user clicks on the private keeper no email radio button
     When the user press the submit control
     Then there will be an error message displayed "Date of Birth - Must be a valid date DD MM YYYY and not be in the future."

   Scenario: - Date of birth - Past date
     Given the Date of birth is more than oneHundredTen years in the past
     When the user clicks on the private keeper no email radio button
     When the user press the submit control
     Then there will be an errored message  "Date of Birth - Date of birth cannot be more than 110 years in the past"

   Scenario: - Driver Number capitalisation
     Given that the user is on the Private Keeper details page
     When the user enters a character into the Driver Number field
     Then the character is capitalised

   Scenario: - Driver number - format error
     Given that the user is on the Private Keeper details page
     When the user has entered a driver number into the "Driver number" control
     Then there will be an error message displayed as "Driver number - The Driver Number that you have entered does not appear to be in an acceptable format. Please amend your entry or delete and re-submit"

   Scenario: - Keeper email - Valid
     Given that the user is on the Private Keeper details page
     When the user enters a valid email address
     Then the user will be able to submit the valid email address of up to "254" characters

   Scenario: - Keeper email -  Null
     Given that the user is on the Private Keeper details page
     When the user has not entered an email address and select the submit control
     Then the system will not display an error message "Email - Must be a valid email address"

   Scenario: - Keeper email - Invalid
     Given that the user is on the Private Keeper details page
     When the user has  entered an invalid email address and select the submit control
     Then the system will display an error for invaild email address "Must be a valid email address"
