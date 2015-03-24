 @tag
  Feature:Manual Adress entry for keeper

    Scenario:- Enter Address - Data capture
      Given that the user is on the Enter Address page
      When the user tries to enter the new keeper address
      Then the user will have the field labels "Building/number and street" Line two of address with no label Line three of address with no label Town or City with field label "Town or city" Postcode  with field label "Postcode"

    Scenario: - Enter address - Hint text
      Given that the user is on the Enter Address page
      When the user tries to enter the new keeper address
      Then there will be hint text stating "e.g. 1 HIGH STREET" below the field Building name/number and street

    Scenario: - Address entry - valid format
      Given the address is not blank and has a valid format
      When the user has selected the submit control
      Then there is no address error message is displayed "Building/number and street must contain between 4 and 30 characters"
      And the trader details are retained

    Scenario: - Address entry - line 1 less than 4 characters
      Given the data in Line one of the address has less than 4 characters
      When the user has selected the submit control
      Then an error message is displayed "Building/number and street must contain between 4 and 30 characters"

    Scenario: - Address entry - Town or city - null or less than three characters
      Given the town or city is null OR the town or city has less than 3 characters
      When the user has selected the submit control
      Then there is a error message displayed "Town or city must contain between 3 and 20 characters"

    Scenario: - Postcode - non editable
      Given the user has entered a postcode on either the private or business keeper page
      When the manual address page is invoked
      Then the postcode field is prepopulated and is non editable

    Scenario:
      Given the user is on the manual address page
      When the user has selected the submit control
      Then the user is taken to the Date of Sale page

    Scenario:
      Given the user is on the manual address page
      When the user has selected the Back control
      Then the user is taken to the previous Address not found page

