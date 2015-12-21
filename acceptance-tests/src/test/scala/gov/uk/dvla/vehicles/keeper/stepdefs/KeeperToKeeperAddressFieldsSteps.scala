package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDriver}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class KeeperToKeeperAddressFieldsSteps(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToEnterKeeperAddressManuallyPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    pageTitle shouldEqual  PrivateKeeperDetailsPage.title withClue trackingId
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox.value = "tue"
    PrivateKeeperDetailsPage.lastNameTextBox.value = "nny"
    PrivateKeeperDetailsPage.postcodeTextBox.value = "qq99qw"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  @Given("^that the user is on the Enter Address page$")
  def that_the_user_is_on_the_Enter_Address_page()  {
    goToEnterKeeperAddressManuallyPage()
  }

  @When("^the user tries to enter the new keeper address$")
  def the_user_tries_to_enter_the_new_keeper_address()  {
  }

  @Then("^the user will have the field labels \"(.*?)\" Line two of address with no label Line three of address with no label Town or City with field label \"(.*?)\" Postcode  with field label \"(.*?)\"$")
  def the_user_will_have_the_field_labels_Line_two_of_address_with_no_label_Line_three_of_address_with_no_label_Town_or_City_with_field_label_Postcode_with_field_label(a1:String,  a2:String, a3:String)  {
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageSource.contains(a1.trim) shouldBe true withClue trackingId
    pageSource.contains(a2.trim) shouldBe true withClue trackingId
    pageSource.contains(a3.trim) shouldBe true withClue trackingId
  }

  @Then("^there will be hint text stating \"(.*?)\" below the field Building name/number and street$")
  def there_will_be_hint_text_stating_below_the_field_Building_name_number_and_street(a1:String) {
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageSource.contains(a1) shouldBe true withClue trackingId
  }

  @When("^the user has selected the submit control$")
  def the_user_has_selected_the_submit_control()  {
    click on NewKeeperEnterAddressManuallyPage.next
  }

  @Given("^the address is not blank and has a valid format$")
  def the_address_is_not_blank_and_has_a_valid_format()  {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "1 first lane"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "hghjg"
  }

  @Then("^there is no address error message is displayed \"(.*?)\"$")
  def there_is_no_address_error_message_is_displayed(f:String)  {
  }

  @Then("^the trader details are retained$")
  def the_trader_details_are_retained()  {
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
  }

  @Given("^the data in Line one of the address has less than (\\d+) characters$")
  def the_data_in_Line_one_of_the_address_has_less_than_characters(d:Int)  {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "1G"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "hghjg"
  }

  @Then("^an error message is displayed \"(.*?)\"$")
  def an_error_message_is_displayed(errMsgForAddress:String) {
    NewKeeperEnterAddressManuallyPage.errorTextForFields(errMsgForAddress) shouldBe true withClue trackingId
  }

  @Given("^the town or city is null OR the town or city has less than (\\d+) characters$")
  def the_town_or_city_is_null_OR_the_town_or_city_has_less_than_characters(h:Int) {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "df"
  }

  @Then("^there is a error message displayed \"(.*?)\"$")
  def there_is_a_error_message_displayed(errMsgForTown:String)  {
    NewKeeperEnterAddressManuallyPage.errorTextForFields(errMsgForTown) shouldBe true withClue trackingId
  }

  @Given("^the user has entered a postcode on either the private or business keeper page$")
  def the_user_has_entered_a_postcode_on_either_the_private_or_business_keeper_page()  {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "df"
  }

  @Given("^the user is on the manual address page$")
  def the_user_is_on_the_manual_address_page() {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    NewKeeperEnterAddressManuallyPage.addressBuildingNameOrNumber.value = "1Gffhf"
    NewKeeperEnterAddressManuallyPage.addressPostTown.value = "hghjg"
  }

  @Then("^the user is taken to the Date of Sale page$")
  def the_user_is_taken_to_the_date_of_sale_page() {
     pageTitle shouldEqual DateOfSalePage.title withClue trackingId
  }

  @When("^the user has selected the Back control$")
  def the_user_has_selected_the_Back_control() {
    goToEnterKeeperAddressManuallyPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    click on NewKeeperEnterAddressManuallyPage.back
  }

  @Then("^the user is taken to the previous Address not found page$")
  def the_user_is_taken_to_the_previous_Address_not_found_page() {
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    pageSource should include("No address found for that postcode") withClue trackingId
  }
}
