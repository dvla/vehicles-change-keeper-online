package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}
import pages.changekeeper.{BeforeYouStartPage, VehicleLookupPage, VehicleLookupFailurePage, VrmLockedPage}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator

class BruteForceKeeperToKeeperSteps(webBrowserDriver: WebBrowserDriver) extends AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]
  private final val vrmNo = RandomVrmGenerator.vrm
  private final val docRef = RandomVrmGenerator.docRef

  def bruteForceUnsuccessfulPage() {
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.vrm
    VehicleLookupPage.documentReferenceNumber.value =  RandomVrmGenerator.docRef
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.next
    pageTitle shouldEqual VehicleLookupFailurePage.title withClue trackingId
  }

  def bruteForceLockedPage() {
    VehicleLookupPage.vehicleRegistrationNumber.value = vrmNo
    VehicleLookupPage.documentReferenceNumber.value = docRef
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
  }

  @Given("^the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen$")
  def the_user_has_submitted_invalid_combination_of_VRN_DRN_on_vehicle_lookup_screen() {
    go to VehicleLookupPage
    bruteForceUnsuccessfulPage()
  }

  @Given("^the user  has submitted invalid combination of VRN & DRN on vehicle lookup screen to get locked message$")
  def the_user_has_submitted_invalid_combination_of_VRN_DRN_on_vehicle_lookup_screen_to_get_locked_message() {
    go to VehicleLookupPage
    bruteForceLockedPage()
  }

  @When("^the number of sequential attempts for that VRN is less than four times$")
  def the_number_of_sequential_attempts_for_that_VRN_is_less_than_four_times() {
    click on VehicleLookupFailurePage.vehicleLookup
    for (a <- 1 to 2) {
      bruteForceUnsuccessfulPage()
      if (a != 2)
        click on VehicleLookupFailurePage.vehicleLookup
    }
  }

  @Then("^there will be an error message displayed see error message \"(.*?)\"$")
  def there_will_be_an_error_message_displayed_see_error_message(unsucFulMsg: String) {
    VehicleLookupFailurePage.errorTextForTitle(unsucFulMsg) shouldEqual true withClue trackingId
  }

  @Then("^the primary action control is \"(.*?)\" which will take the user back to the vehicle look-up screen with the original VRM & DRN data pre-populated$")
  def the_primary_action_control_is_which_will_take_the_user_back_to_the_vehicle_look_up_screen_with_the_original_VRM_DRN_data_pre_populated(s: String) {
    click on VehicleLookupFailurePage.vehicleLookup
    pageTitle shouldEqual VehicleLookupPage.title withClue trackingId
  }

  @Then("^the secondary action control is to \"(.*?)\" the service which will take the user to the GDS driving page$")
  def the_secondary_action_control_is_to_the_service_which_will_take_the_user_to_the_GDS_driving_page(D: String) {
    pageTitle shouldEqual VrmLockedPage.title withClue trackingId
    click on VrmLockedPage.exit
    pageTitle shouldEqual BeforeYouStartPage.title withClue trackingId
  }

  @When("^the number of sequential attempts for that VRN is more than three times$")
  def the_number_of_sequential_attempts_for_that_VRN_is_more_than_three_times(): Unit = {
    pageTitle shouldEqual VehicleLookupFailurePage.title withClue trackingId
    click on VehicleLookupFailurePage.vehicleLookup
    for (a <- 1 to 3) {
      bruteForceLockedPage()
      if (a != 3)
        click on VehicleLookupFailurePage.vehicleLookup
    }
  }

  @Then("^there will be an error message display see error message \"(.*?)\"$")
  def there_will_be_an_error_message_display_see_error_message(msg: String): Unit = {
     pageTitle shouldEqual VrmLockedPage.title withClue trackingId
  }
}
