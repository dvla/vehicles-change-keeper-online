@tag
  Feature:BruteForce for Keeper to Keeper application

    Scenario: VRN/DRN combo invalid to check UnSuccesfull message for first 3 attempts
      Given the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen
      When the number of sequential attempts for that VRN is less than four times
      Then there will be an error message displayed see error message "Unable to find a vehicle record"
      And the primary action control is "Try Again" which will take the user back to the vehicle look-up screen with the original VRM & DRN data pre-populated

    Scenario: VRN/DRN combo invalid to check Registration Number Locked message after three UnSuccesful attempts
      Given the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen to get locked message
      When the number of sequential attempts for that VRN is more than three times
      Then there will be an error message display see error message "Registration number is locked"
      And the secondary action control is to "Exit" the service which will take the user to the GDS driving page

