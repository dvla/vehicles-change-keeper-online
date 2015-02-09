@tag
  Feature:Private keeper postcode validation

    Scenario: Poscode Invalid
      Given that the user is on the Private Keeper details page
      When the user enters an invalid postcode
      Then the user will see an error message "Postcode - Must be between five and eight characters and in a valid format, e.g. AB1 2BA or AB12BA"

    Scenario: Postcode -  Null
      Given that the user is on the Private Keeper details page
      When the user enters an null in  postcode textbox
      Then the user will see an error message "Postcode - Must be between five and eight characters and in a valid format, e.g. AB1 2BA or AB12BA"

    Scenario: Postcode - Valid
      Given that the user is on the Private Keeper details page
      When the user enters an valid  postcode
      Then the user will presented with a list of addresses

    Scenario: Find Address
      Given that the user is on the Private Keeper details page
      When the user selects the next button and no errors persist
      Then the user is taken to either postcode lookup success or postcode lookup failure screen

    Scenario:  Back
      Given that the user is on the Private Keeper details page
      When the user selects the 'Back' button and no errors persist
      Then the user is taken to the previous page