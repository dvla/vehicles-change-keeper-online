package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.{NewKeeperChooseYourAddressPage, PrivateKeeperDetailsPage, VehicleLookupPage}

class FieldLengthValidationTest(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToPrivateKeeperDetailsPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "B1"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual PrivateKeeperDetailsPage.title
  }

  @Given("^that a customer has selected the any title button in private keeper details page$")
  def that_a_customer_has_selected_the_any_title_button_in_private_keeper_details_page()  {
    goToPrivateKeeperDetailsPage()
    click on PrivateKeeperDetailsPage.mr
  }

  @When("^the total number of characters is equal to or less than \"(.*?)\" inlcuding title, space and first name$")
  def the_total_number_of_characters_is_equal_to_or_less_than_inlcuding_title_space_and_first_name(chars:String)  {
    PrivateKeeperDetailsPage.firstNameTextBox enter "avsreedrtagdtesbgdrewasd"
    val fieldLength:String =  "mr"+' '+PrivateKeeperDetailsPage.firstNameTextBox.value
    fieldLength.length shouldEqual 27
  }

  @When("^all other on page validation is successfully met$")
  def all_other_on_page_validation_is_successfully_met()  {
    PrivateKeeperDetailsPage.lastNameTextBox enter "ghjgj"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99qq"
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^they will proceed to the Select new keeper address page$")
  def they_will_proceed_to_the_Select_new_keeper_address_page() {
    page.title shouldEqual NewKeeperChooseYourAddressPage.title
  }

  @When("^the total number of characters is greater than \"(.*?)\" including title, space and first name$")
  def the_total_number_of_characters_is_greater_than_including_title_space_and_first_name(field:String)  {
    PrivateKeeperDetailsPage.firstNameTextBox enter "avsreedrtagdtesbgdrewasdjhkhh"
    val fieldLength:String =  "mr"+' '+PrivateKeeperDetailsPage.firstNameTextBox.value
    fieldLength.length shouldNot equal(27)
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^they will be presented with an error message displayed \"(.*?)\"$")
  def they_will_be_presented_with_an_error_message_displayed(errMsg:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(errMsg) shouldBe true
  }

}
