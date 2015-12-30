package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import pages.changekeeper.{BusinessKeeperDetailsPage, NewKeeperChooseYourAddressPage, VehicleLookupPage}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, go, find, pageTitle, pageSource, tagName}

class BusinessKeeperDetailsSteps(webBrowserDriver: WebBrowserDriver)
  extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def gotoBusinessKeeperDetailsPage(){
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
  }

  @Given("^that the user has selected Business on the vehicle lookup page$")
  def that_the_user_has_selected_Business_on_the_vehicle_lookup_page() {
    gotoBusinessKeeperDetailsPage()
  }

  @When("^the user is on the new-business-keeper-details page$")
  def the_user_is_on_the_new_business_keeper_details_page() {
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
  }

  @Then("^there is a label titled \"(.*?)\"$")
  def there_is_a_label_titled(fleetNo:String ) {
    pageSource contains fleetNo
  }

  @Then("^there will be a data entry control for fleet number using the format NNNNNN or NNNNN-$")
  def there_will_be_a_data_entry_control_for_fleet_number_using_the_format_NNNNNN_or_NNNNN(): Unit = {
    BusinessKeeperDetailsPage.fleetNumberField.value = "fghfj"
  }

  @Then("^there will be help text displayed above the fleet number field \"(.*?)\"$")
  def there_will_be_help_text_displayed_above_the_fleet_number_field(helpText:String)  {
    pageSource contains helpText
  }

  @Given("^the fleet number is blank in business keeper details page$")
  def the_fleet_number_is_blank_in_business_keeper_details_page()  {
    gotoBusinessKeeperDetailsPage()
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^the user can proceed without an error being displayed for the fleet number$")
  def the_user_can_proceed_without_an_error_being_displayed_for_the_fleet_number()  {
  }

  @Given("^the fleet number is not blank and has a valid format in business keeper deatils page$")
  def the_fleet_number_is_not_blank_and_has_a_valid_format_in_business_keeper_deatils_page()  {
    gotoBusinessKeeperDetailsPage()
    BusinessKeeperDetailsPage.fleetNumberField.value = "345654"
  }

  @Given("^the fleet number has an invalid format in business keeper details page$")
  def the_fleet_number_has_an_invalid_format_in_business_keeper_details_page()  {
    gotoBusinessKeeperDetailsPage()
    click on BusinessKeeperDetailsPage.fleetNumberVisible
    BusinessKeeperDetailsPage.fleetNumberField.value = "345"
  }

  @Then("^there is a fleet number error message displayed \"(.*?)\"$")
  def there_is_a_fleet_number_error_message_displayed(fleetNoErrMsg:String)  {
    find(tagName("body")).get.text should include(fleetNoErrMsg) withClue trackingId
  }

  @When("^the user selects the field labelled Business name$")
  def the_user_selects_the_field_labelled_Business_name() {
  }

  @Then("^the user can enter a business name of up to (\\d+) characters$")
  def the_user_can_enter_a_business_name_of_up_to_characters(charLength:Int) {
    BusinessKeeperDetailsPage.businessNameField.value = "abcdefghijklmnopqrstsdgfhtajs"
  }

  @Given("^the business name contains invalid characters$")
  def the_business_name_contains_invalid_characters()  {
    gotoBusinessKeeperDetailsPage()
    BusinessKeeperDetailsPage.businessNameField.value = "h"
    BusinessKeeperDetailsPage.postcodeField.value = "we32we"
  }

  @When("^the user has selected the submit control on the new-business-keeper-details screen$")
  def the_user_has_selected_the_submit_control_on_the_new_business_keeper_details_screen() {
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^the user will receive an error message \"(.*?)\"$")
  def the_user_will_receive_an_error_message(businessNameErrMsg: String)  {
    find(tagName("body")).get.text should include(businessNameErrMsg) withClue trackingId
  }

  @Given("^the user has entered values into the business name$")
  def the_user_has_entered_values_into_the_business_name()  {
    gotoBusinessKeeperDetailsPage()
    BusinessKeeperDetailsPage.businessNameField.value = "dhdhh"
  }

  @When("^the user select the submit control$")
  def the_user_select_the_submit_control() {
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^invalid white space will be stripped from the start and end of the business name but spaces are allowed within the business name$")
  def invalid_white_space_will_be_stripped_from_the_start_and_end_of_the_business_name_but_spaces_are_allowed_within_the_business_name()  {
    BusinessKeeperDetailsPage.businessNameField.value.length shouldEqual 5 withClue trackingId
  }

  @Then("^validation will be done on the entered text once the white space has been removed$")
  def validation_will_be_done_on_the_entered_text_once_the_white_space_has_been_removed()  {
  }

  @Given("^that the user is on the Enter business keeper details page$")
  def that_the_user_is_on_the_Enter_business_keeper_details_page()  {
    gotoBusinessKeeperDetailsPage()
  }

  @When("^the user select the field labelled Email address$")
  def the_user_select_the_field_labelled_Email_address()  {
  }

  @Then("^the user clicks on the no email radio button$")
  def the_user_clicks_on_the_no_email_radio_button()  {
    click on BusinessKeeperDetailsPage.emailInvisible
  }

  @Then("^the user will be able to enter an email address of up to (\\d+) characters$")
  def the_user_will_be_able_to_enter_an_email_address_of_up_to_characters(charLength:Int)  {
    click on BusinessKeeperDetailsPage.emailVisible
    BusinessKeeperDetailsPage.emailField.value = "jfejfhhfjlshgljhgjlhljhljhjlhljhjlhljh@fdgfdgfhgfhghgfhgfhgfhfghgfhg"
    BusinessKeeperDetailsPage.emailConfirmField.value =
      "jfejfhhfjlshgljhgjlhljhljhjlhljhjlhljh@fdgfdgfhgfhghgfhgfhgfhfghgfhg"
  }

  @Given("^the user has not entered an email address$")
  def the_user_has_not_entered_an_email_address()  {
  }

  @Then("^the system will not display an error for missing or invalid email address$")
  def the_system_will_not_display_an_error_for_missing_or_invalid_email_address()  {
    find(tagName("body")).get.text should not include "noerrror" withClue trackingId
  }

  @Then("^the system will display an error for invalid email address \"(.*?)\"$")
  def the_system_will_display_an_error_for_invalid_email_address(emailErrMsg: String) {
    find(tagName("body")).get.text should include(emailErrMsg) withClue trackingId
  }

  @Given("^the user has entered an invalid email address$")
  def the_user_has_entered_an_invalid_email_address() {
    click on BusinessKeeperDetailsPage.emailVisible
    BusinessKeeperDetailsPage.emailField.value = "aa"
    BusinessKeeperDetailsPage.emailConfirmField.value = "aa"
  }

  @When("^the user tries to search on an invalid postcode$")
  def the_user_tries_to_search_on_an_invalid_postcode() {
    BusinessKeeperDetailsPage.postcodeField.value = "adsadsds"
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
  }
  @Then("^the user does not progress to the next stage of the service$")
  def the_user_does_not_progress_to_the_next_stage_of_the_service() {
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
  }

  @When("^the user tries to search on a blank postcode$")
  def the_user_tries_to_search_on_a_blank_postcode()  {
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
  }

  @When("^the user tries to search on a valid postcode$")
  def the_user_tries_to_search_on_a_valid_postcode() {
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "dvdvvv"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^the user is presented with a list of matching addresses$")
  def the_user_is_presented_with_a_list_of_matching_addresses() {
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  @Then("^an error message is displays \"(.*?)\"$")
  def an_error_message_is_displayed(postcodErrMsg:String)  {
    find(tagName("body")).get.text should include(postcodErrMsg) withClue trackingId
  }

  @When("^the user enters special characters in businessname with valid data in rest of the fields$")
  def the_user_enters_special_characters_in_businessname_with_valid_data_in_rest_of_the_fields()  {
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    pageTitle shouldEqual  BusinessKeeperDetailsPage.title withClue trackingId
    BusinessKeeperDetailsPage.businessNameField.value = "hgff(&/,)"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^the user will sucessfully navigate to next page$")
  def the_user_will_sucessfully_navigate_to_next_page()  {
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  @When("^the user enters special characters at the start of the business name$")
  def the_user_enters_special_charcters_at_the_start_of_the_business_name()  {
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    pageTitle shouldEqual  BusinessKeeperDetailsPage.title withClue trackingId
    BusinessKeeperDetailsPage.businessNameField.value = "(&/,)GFHF"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
  }

  @Then("^will remain in the same page instead of progress to next page$")
  def will_remain_in_the_same_page_instead_of_progress_to_next_page()  {
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
  }
}
