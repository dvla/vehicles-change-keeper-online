@tag
  Feature:
    Scenario: VRN/DRN combo invalid
      Given the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen
      When the number of sequential attempts for that VRN is less than four times
      Then there will be an error message displayed see error message "Look-up was unsuccessful"
      And the primary action control is "Try Again" which will take the user back to the vehicle look-up screen with the original VRM & DRN data pre-populated
      And the secondary action control is to "Exit" the service which will take the user to the GDS driving page

    Scenario: VRN/DRN combo invalid
       Given the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen
       When the number of sequential attempts for that VRN is more than three times
       Then there will be an error message display see error message "Registration number is locked"
       And the primary action control is "Try Again" which will take the user back to the vehicle look-up screen with the original VRM & DRN data pre-populated
       And the secondary action control is to "Exit" the service which will take the user to the GDS driving page
