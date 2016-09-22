package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Given,Then, When}
import java.util.Calendar
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator

class HappyAndSadPathSteps(webBrowserDriver: WebBrowserDriver)
  extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  val dos = Calendar.getInstance()
  dos.add(Calendar.YEAR, -1)

  @Given("^the user is on the vehicle look up page$")
  def the_user_is_on_the_vehicle_look_up_page() {
    go to VehicleLookupPage
  }

  @When("^the keeper sold the vehicle to the private keeper after entering valid registration and doc ref number and click on submit button$")
  def the_keeper_sold_the_vehicle_to_the_private_keeper_after_entering_valid_registration_and_doc_ref_number_and_click_on_submit_button() {
    pageTitle shouldEqual VehicleLookupPage.title withClue trackingId
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
  }

  @When("^the user is on Private Keeper details page and entered through successful postcode lookup$")
  def the_user_is_on_Private_Keeper_details_page_and_entered_through_successful_postcode_lookup() {
    pageTitle shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox.value = "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox.value = "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox.value = "qq99qq"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    NewKeeperChooseYourAddressPage.chooseAddress.value = NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
  }

  @Then("^the user will be on complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
    enterDosDate()
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.regRight
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to private keeper succesful summary page$")
  def the_user_will_be_taken_to_private_keeper_succesful_summary_page() {
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the keeper sold the vehicle to the Business keeper after entering valid registration and doc ref number and click on submit button$")
  def the_keeper_sold_the_vehicle_to_the_Business_keeper_after_entering_valid_registration_and_doc_ref_number_and_click_on_submit_button() {
    pageTitle shouldEqual VehicleLookupPage.title withClue trackingId
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
  }

  @When("^the user is on Business Keeper details page and entered through successful postcode lookup$")
  def the_user_is_on_Business_Keeper_details_page_and_entered_through_successful_postcode_lookup() {
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "trading"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    NewKeeperChooseYourAddressPage.chooseAddress.value = NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
  }

  @Then("^the user will be on Business keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_Business_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
    enterDosDate()
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.regRight
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Business keeper succesful summary page$")
  def the_user_will_be_taken_to_Business_keeper_succesful_summary_page() {
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup$")
  def the_trader_entered_through_unsuccessful_postcode_lookup() {
    pageTitle shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox.value = "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox.value = "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox.value = "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageTitle shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "swansea"
    NewKeeperEnterAddressManuallyPage.addressPostcode.value = "sa11aa"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be taken to  unsuccesful postcode Business keeper succesful summary page$")
  def the_user_will_be_taken_to_unsuccesful_postcode_Business_keeper_succesful_summary_page() {
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup business user$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_business_user() {
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "trading"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99kj"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageTitle shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "swansea"
    NewKeeperEnterAddressManuallyPage.addressPostcode.value = "sa11aa"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Business keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_unsuccesful_postcode_Business_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
    enterDosDate()
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.regRight
    click on CompleteAndConfirmPage.next
  }

  @When("^the trader entered through unsuccessful postcode lookup private keeper$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_private_keeper() {
    pageTitle shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox.value = "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox.value = "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox.value = "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageTitle shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "swansea"
    NewKeeperEnterAddressManuallyPage.addressPostcode.value = "sa11aa"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Private keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_unsuccesful_postcode_Private_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button()  {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
    enterDosDate()
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.regRight
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Unsuccesful postcode private keeper details page succesful summary page$")
  def the_user_will_be_taken_to_Unsuccesful_postcode_private_keeper_details_page_succesful_summary_page() {
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup private keeper failure data$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_private_keeper_failure_data()  {
    pageTitle shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox.value = "testcase"
    PrivateKeeperDetailsPage.lastNameTextBox.value = "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox.value = "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageTitle shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "swansea"
    NewKeeperEnterAddressManuallyPage.addressPostcode.value = "sa11aa"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Private keeper complete and confirm page and click on confirm sale button with failure data$")
  def the_user_will_be_on_unsuccesful_postcode_Private_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button_with_failure_data()  {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
    enterDosDate()
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.regRight
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Unsuccesful postcode private keeper details page failure summary page$")
  def the_user_will_be_taken_to_Unsuccesful_postcode_private_keeper_details_page_failure_summary_page(): Unit = {
    pageSource.contains("Transaction Unsuccessful")
  }

  private def enterDosDate() {
    DateOfSalePage.dayDateOfSaleTextBox.value = f"${dos.get(Calendar.DATE)}%02d"  //must be dd format
    DateOfSalePage.monthDateOfSaleTextBox.value = f"${(dos.get(Calendar.MONTH) + 1)}%02d" //must be mm format
    DateOfSalePage.yearDateOfSaleTextBox.value = dos.get(Calendar.YEAR).toString
  }
}
