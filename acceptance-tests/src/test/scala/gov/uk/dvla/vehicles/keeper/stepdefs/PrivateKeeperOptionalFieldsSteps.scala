package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.{PrivateKeeperDetailsPage, VehicleLookupPage}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class PrivateKeeperOptionalFieldsSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToPrivateKeeperDetailsPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "B1"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual PrivateKeeperDetailsPage.title
  }

  @Given("^the user enters a validate date of birth$")
  def the_user_enters_a_validate_date_of_birth()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox enter "12"
    PrivateKeeperDetailsPage.monthDateOfBirthTextBox enter "10"
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox  enter "1985"
  }

  @Then("^the user will not see any error message like \"(.*?)\"$")
  def the_user_will_not_see_any_error_message_like(noErrMsg:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(noErrMsg) shouldBe false
  }

  @Given("^the user enters a invalid date of birth  and no other errors persists$")
  def the_user_enters_a_invalid_date_of_birth_and_no_other_errors_persists()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox enter "42"
    PrivateKeeperDetailsPage.monthDateOfBirthTextBox enter "10"
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox  enter "1985"
  }

  @When("^the user press the submit control$")
  def the_user_press_the_submit_control()  {
      click on PrivateKeeperDetailsPage.next
  }

  @Then("^there will be an error message displayed \"(.*?)\"$")
  def there_will_be_an_error_message_displayed(errMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMsg) shouldBe true
  }

  @Given("^the user enters the dateOfBirth in future$")
  def the_user_enters_the_dateOfBirth_in_future() {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox enter "14"
    PrivateKeeperDetailsPage.monthDateOfBirthTextBox enter "10"
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox  enter "2017"
  }

  @Given("^the Date of birth is more than oneHundredTen years in the past$")
  def the_Date_of_birth_is_more_than_oneHundredTen_years_in_the_past()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox enter "14"
    PrivateKeeperDetailsPage.monthDateOfBirthTextBox enter "10"
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox  enter "1900"
  }

  @Then("^there will be an error message  \"(.*?)\"$")
  def there_will_be_an_error_message(dobFutureError:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(dobFutureError) shouldBe true
  }


  @Then("^there will be an errored message  \"(.*?)\"$")
  def there_will_be_an_errored_message(dobPastError:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(dobPastError) shouldBe true
  }

  @Given("^that the user is on the Private Keeper details page$")
  def that_the_user_is_on_the_Private_Keeper_details_page()  {
    goToPrivateKeeperDetailsPage()
  }

  @When("^the user enters a character into the Driver Number field$")
  def the_user_enters_a_character_into_the_Driver_Number_field() {
    PrivateKeeperDetailsPage.driverNumberTextBox enter "morga657052Sm9jk"
  }

  @Then("^the character is capitalised$")
  def the_character_is_capitalised()  {
    PrivateKeeperDetailsPage.driverNumberTextBox.value.toUpperCase shouldEqual "MORGA657052SM9JK"
  }

  @When("^the user has entered a driver number into the \"(.*?)\" control$")
  def the_user_has_entered_a_driver_number_into_the_control(g:String)  {
    PrivateKeeperDetailsPage.driverNumberTextBox enter "morga657052Sm96876"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^there will be an error message displayed as \"(.*?)\"$")
  def there_will_be_an_error_message_displayed_as(driverErrorMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(driverErrorMsg) shouldBe true
  }

  @When("^the user enters a valid email address$")
  def the_user_enters_a_valid_email_address()  {
    click on PrivateKeeperDetailsPage.emailVisible
    PrivateKeeperDetailsPage.emailTextBox enter "a@gmail.com"
  }

  @Then("^the user will be able to submit the valid email address of up to \"(.*?)\" characters$")
  def the_user_will_be_able_to_submit_the_valid_email_address_of_up_to_characters(chars:String) {
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user has not entered an email address and select the submit control$")
  def the_user_has_not_entered_an_email_address_and_select_the_submit_control()  {
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the system will not display an error message \"(.*?)\"$")
  def the_system_will_not_display_an_error_message(noErrorMsg:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(noErrorMsg) shouldBe false
  }

  @When("^the user has  entered an invalid email address and select the submit control$")
  def the_user_has_entered_an_invalid_email_address_and_select_the_submit_control()  {
    click on PrivateKeeperDetailsPage.emailVisible
    PrivateKeeperDetailsPage.emailTextBox enter "acom"
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the system will display an error for invaild email address \"(.*?)\"$")
  def the_system_will_display_an_error_for_invaild_email_address(errMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMsg) shouldBe true
  }
}
