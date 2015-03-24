@tag
  Feature:complete and confirm page field validation

    Background:
      Given that the user is on the complete and confirm page


    Scenario:complete and confirm-consent checkbox unchecked
      When the consent field is not checked
      And   the user click on confirm sale button
      Then  an error message displayed "You must have the consent of the new keeper to notify DVLA of the purchase of this vehicle"
      And  the user is not progressed to the next page

    Scenario:complete and confirm-consent checkbox checked
      When the consent field is checked
      And   the user click on confirm sale button
      Then the user is progressed to the next stage of the service
