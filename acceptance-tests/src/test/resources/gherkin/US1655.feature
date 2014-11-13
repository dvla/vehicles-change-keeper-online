@tag
  Feature:
    Scenario:-Less than or equal  27 characters - Pass
      Given that a customer has selected the any title button in private keeper details page
      When the total number of characters is equal to or less than "27" inlcuding title, space and first name
      And all other on page validation is successfully met
      Then they will proceed to the Select new keeper address page

    Scenario:-More than 27 characters - Fail
      Given that a customer has selected the any title button in private keeper details page
      When the total number of characters is greater than "27" including title, space and first name
      Then they will be presented with an error message displayed "The combined length of your Title e.g. Mr/Mrs/Miss etc and First name(s) cannot exceed 26 characters. Please amend your entry and re-submit"