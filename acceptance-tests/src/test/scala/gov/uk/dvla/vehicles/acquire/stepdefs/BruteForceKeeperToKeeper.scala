package gov.uk.dvla.vehicles.acquire.stepdefs


import cucumber.api.scala.{EN, ScalaDsl}
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._


final class BruteForceKeeperToKeeper(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToPrivateKeeperDetailsPage() {

    VehicleLookupPage.vehicleRegistrationNumber enter "d1"
    VehicleLookupPage.documentReferenceNumber enter "11111111114"
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual (VehicleLookupFailurePage.title)
  }

  @Given("^the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen$")
  def the_user_has_submitted_invalid_combination_of_VRN_DRN_on_vehicle_lookup_screen()  {
    /*go to VehicleLookupPage
    goToPrivateKeeperDetailsPage()*/
  }

  @When("^the number of sequential attempts for that VRN is less than four times$")
  def the_number_of_sequential_attempts_for_that_VRN_is_less_than_four_times() {
       /* click on VehicleLookupFailurePage.vehicleLookup
        var a=0
        for( a <- 1 to 2){
          goToPrivateKeeperDetailsPage()
          if(a!=2)
          click on VehicleLookupFailurePage.vehicleLookup
        }*/

  }
  @Then("^there will be an error message displayed see error message \"(.*?)\"$")
  def there_will_be_an_error_message_displayed_see_error_message(unsucFulMsg:String) {

     // VehicleLookupFailurePage.errorTextForTitle(unsucFulMsg) shouldEqual (true)
  }

  @Then("^the primary action control is \"(.*?)\" which will take the user back to the vehicle look-up screen with the original VRM & DRN data pre-populated$")
  def the_primary_action_control_is_which_will_take_the_user_back_to_the_vehicle_look_up_screen_with_the_original_VRM_DRN_data_pre_populated(s:String)  {

    /*click on VehicleLookupFailurePage.vehicleLookup
    page.title shouldEqual(VehicleLookupPage.title)*/
  }

  @Then("^the secondary action control is to \"(.*?)\" the service which will take the user to the GDS driving page$")
  def the_secondary_action_control_is_to_the_service_which_will_take_the_user_to_the_GDS_driving_page(D:String) {
    /*click on VehicleLookupPage.next
    click on VehicleLookupFailurePage.beforeYouStart
    page.title shouldEqual(BeforeYouStartPage.title)*/
  }

  @When("^the number of sequential attempts for that VRN is more than three times$")
  def the_number_of_sequential_attempts_for_that_VRN_is_more_than_three_times() {
    /*click on VehicleLookupFailurePage.vehicleLookup
    var a=0
    for( a <- 1 to 3){
      goToPrivateKeeperDetailsPage()
      if(a!=3)
        click on VehicleLookupFailurePage.vehicleLookup
    }
  }*/}

  @Then("^there will be an error message display see error message \"(.*?)\"$")
  def there_will_be_an_error_message_display_see_error_message(msg:String) {

    //page.title shouldEqual VrmLockedPage.title
  }


}
