package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Given,Then, When}
import helpers.RandomVrmGenerator
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDSL, WebBrowserDriver}

class HappyAndSadPathSteps(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with WebBrowserDSL with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  @Given("^the user is on the vehicle look up page$")
  def the_user_is_on_the_vehicle_look_up_page() {
    go to VehicleLookupPage
  }

  @When("^the keeper sold the vehicle to the private keeper after entering valid registration and doc ref number and click on submit button$")
  def the_keeper_sold_the_vehicle_to_the_private_keeper_after_entering_valid_registration_and_doc_ref_number_and_click_on_submit_button() {
    page.title shouldEqual VehicleLookupPage.title withClue trackingId
    VehicleLookupPage.vehicleRegistrationNumber enter RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
  }

  @When("^the user is on Private Keeper details page and entered through successful postcode lookup$")
  def the_user_is_on_Private_Keeper_details_page_and_entered_through_successful_postcode_lookup() {
    page.title shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox enter "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99qq"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value = "0"
    click on NewKeeperChooseYourAddressPage.next
  }

  @Then("^the user will be on complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    page.title shouldEqual DateOfSalePage.title withClue trackingId
    DateOfSalePage.dayDateOfSaleTextBox enter "11"
    DateOfSalePage.monthDateOfSaleTextBox enter "11"
    DateOfSalePage.yearDateOfSaleTextBox enter "2011"
    click on DateOfSalePage.next
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to private keeper succesful summary page$")
  def the_user_will_be_taken_to_private_keeper_succesful_summary_page() {
    page.title shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the keeper sold the vehicle to the Business keeper after entering valid registration and doc ref number and click on submit button$")
  def the_keeper_sold_the_vehicle_to_the_Business_keeper_after_entering_valid_registration_and_doc_ref_number_and_click_on_submit_button() {
    page.title shouldEqual VehicleLookupPage.title withClue trackingId
    VehicleLookupPage.vehicleRegistrationNumber enter RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
  }

  @When("^the user is on Business Keeper details page and entered through successful postcode lookup$")
  def the_user_is_on_Business_Keeper_details_page_and_entered_through_successful_postcode_lookup() {
    page.title shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "trading"
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value = "0"
    click on NewKeeperChooseYourAddressPage.next
  }

  @Then("^the user will be on Business keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_Business_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    page.title shouldEqual DateOfSalePage.title withClue trackingId
    DateOfSalePage.dayDateOfSaleTextBox enter "11"
    DateOfSalePage.monthDateOfSaleTextBox enter "11"
    DateOfSalePage.yearDateOfSaleTextBox enter "2011"
    click on DateOfSalePage.next
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Business keeper succesful summary page$")
  def the_user_will_be_taken_to_Business_keeper_succesful_summary_page() {
    page.title shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup$")
  def the_trader_entered_through_unsuccessful_postcode_lookup() {
    page.title shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox enter "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    page.title shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber enter "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown enter "swansea"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be taken to  unsuccesful postcode Business keeper succesful summary page$")
  def the_user_will_be_taken_to_unsuccesful_postcode_Business_keeper_succesful_summary_page() {
    page.title shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup business user$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_business_user() {
    page.title shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "trading"
    BusinessKeeperDetailsPage.postcodeField enter "qq99kj"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.manualAddress
    page.title shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber enter "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown enter "swansea"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Business keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_unsuccesful_postcode_Business_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button() {
    page.title shouldEqual DateOfSalePage.title withClue trackingId
    DateOfSalePage.dayDateOfSaleTextBox enter "11"
    DateOfSalePage.monthDateOfSaleTextBox enter "11"
    DateOfSalePage.yearDateOfSaleTextBox enter "2011"
    click on DateOfSalePage.next
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
  }

  @When("^the trader entered through unsuccessful postcode lookup private keeper$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_private_keeper() {
    page.title shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "ramu"
    PrivateKeeperDetailsPage.lastNameTextBox enter "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    page.title shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber enter "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown enter "swansea"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Private keeper complete and confirm page and click on confirm sale button$")
  def the_user_will_be_on_unsuccesful_postcode_Private_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button()  {
    page.title shouldEqual DateOfSalePage.title withClue trackingId
    DateOfSalePage.dayDateOfSaleTextBox enter "11"
    DateOfSalePage.monthDateOfSaleTextBox enter "11"
    DateOfSalePage.yearDateOfSaleTextBox enter "2011"
    click on DateOfSalePage.next
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Unsuccesful postcode private keeper details page succesful summary page$")
  def the_user_will_be_taken_to_Unsuccesful_postcode_private_keeper_details_page_succesful_summary_page() {
    page.title shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @When("^the trader entered through unsuccessful postcode lookup private keeper failure data$")
  def the_trader_entered_through_unsuccessful_postcode_lookup_private_keeper_failure_data()  {
    page.title shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "testcase"
    PrivateKeeperDetailsPage.lastNameTextBox enter "reddy"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99hj"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.manualAddress
    page.title shouldEqual NewKeeperEnterAddressManuallyPage.title withClue trackingId
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber enter "2 high street"
    NewKeeperEnterAddressManuallyPage.addressPostTown enter "swansea"
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Then("^the user will be on unsuccesful postcode Private keeper complete and confirm page and click on confirm sale button with failure data$")
  def the_user_will_be_on_unsuccesful_postcode_Private_keeper_complete_and_confirm_page_and_click_on_confirm_sale_button_with_failure_data()  {
    page.title shouldEqual DateOfSalePage.title withClue trackingId
    DateOfSalePage.dayDateOfSaleTextBox enter "11"
    DateOfSalePage.monthDateOfSaleTextBox enter "11"
    DateOfSalePage.yearDateOfSaleTextBox enter "2011"
    click on DateOfSalePage.next
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user will be taken to Unsuccesful postcode private keeper details page failure summary page$")
  def the_user_will_be_taken_to_Unsuccesful_postcode_private_keeper_details_page_failure_summary_page(): Unit = {
    page.source.contains("Transaction Unsuccessful")
  }
}
