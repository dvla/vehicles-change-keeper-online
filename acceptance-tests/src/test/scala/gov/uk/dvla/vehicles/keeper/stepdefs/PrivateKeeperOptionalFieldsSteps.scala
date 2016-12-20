package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper
import java.util.Calendar
import org.openqa.selenium.WebDriver
import pages.changekeeper.{PrivateKeeperDetailsPage, VehicleLookupPage}
import pages.common.ErrorPanel
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}

class PrivateKeeperOptionalFieldsSteps(webBrowserDriver: WebBrowserDriver) extends AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  val dob = Calendar.getInstance()
  val age = 20
  val invalidAge = 111
  dob.set(12, 10, Calendar.getInstance().get(Calendar.YEAR) - age)

  def goToPrivateKeeperDetailsPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = RandomVrmGenerator.uniqueVrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    pageTitle shouldEqual PrivateKeeperDetailsPage.title withClue trackingId
  }

  @Given("^the user enters a validate date of birth$")
  def the_user_enters_a_validate_date_of_birth()  {
    goToPrivateKeeperDetailsPage()
    enterDobDate()
  }

  @Then("^the user will not see any error message like \"(.*?)\"$")
  def the_user_will_not_see_any_error_message_like(noErrMsg:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(noErrMsg) shouldBe false withClue trackingId
  }

  @Given("^the user enters a invalid date of birth and no other errors persists$")
  def the_user_enters_a_invalid_date_of_birth_and_no_other_errors_persists()  {
    goToPrivateKeeperDetailsPage()
    enterDobDate()
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox.value = "42"
  }

  @When("^the user press the submit control$")
  def the_user_press_the_submit_control()  {
      click on PrivateKeeperDetailsPage.next
  }

  @Then("^there will be an error message displayed \"(.*?)\"$")
  def there_will_be_an_error_message_displayed(errMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMsg) shouldBe true withClue trackingId
  }

  @Given("^the user enters the dateOfBirth in future$")
  def the_user_enters_the_dateOfBirth_in_future() {
    goToPrivateKeeperDetailsPage()
    enterDobDate()
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox .value = (Calendar.getInstance().get(Calendar.YEAR) + 1).toString
  }

  @Then("^there will be an error message  \"(.*?)\"$")
  def there_will_be_an_error_message(dobFutureError:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(dobFutureError) shouldBe true withClue trackingId
  }

  @Given("^the Date of birth is more than oneHundredTen years in the past$")
  def the_Date_of_birth_is_more_than_oneHundredTen_years_in_the_past()  {
    goToPrivateKeeperDetailsPage()
    enterDobDate()
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox .value = (Calendar.getInstance().get(Calendar.YEAR) - invalidAge).toString
  }

  @Then("^there will be an errored message  \"(.*?)\"$")
  def there_will_be_an_errored_message(dobPastError:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(dobPastError) shouldBe true withClue trackingId
  }

  @Given("^that the user is on the Private Keeper details page$")
  def that_the_user_is_on_the_Private_Keeper_details_page()  {
    goToPrivateKeeperDetailsPage()
  }

  @When("^the user enters a character into the Driver Number field$")
  def the_user_enters_a_character_into_the_Driver_Number_field() {
    PrivateKeeperDetailsPage.driverNumberTextBox.value = "morga657052Sm9jk"
  }

  @Then("^the character is capitalised$")
  def the_character_is_capitalised()  {
    PrivateKeeperDetailsPage.driverNumberTextBox.value.toUpperCase shouldEqual "MORGA657052SM9JK" withClue trackingId
  }

  @When("^the user has entered a driver number into the \"(.*?)\" control$")
  def the_user_has_entered_a_driver_number_into_the_control(g:String)  {
    PrivateKeeperDetailsPage.driverNumberTextBox.value = "morga657052Sm96876"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^there will be an error message displayed as \"(.*?)\"$")
  def there_will_be_an_error_message_displayed_as(driverErrorMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(driverErrorMsg) shouldBe true withClue trackingId
  }

  @When("^the user enters a valid email address$")
  def the_user_enters_a_valid_email_address()  {
    click on PrivateKeeperDetailsPage.emailVisible
    PrivateKeeperDetailsPage.emailTextBox.value = "a@email.com"
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
    PrivateKeeperDetailsPage.errorTextForTitle(noErrorMsg) shouldBe false withClue trackingId
  }

  @When("^the user has entered an invalid email address and select the submit control$")
  def the_user_has_entered_an_invalid_email_address_and_select_the_submit_control()  {
    click on PrivateKeeperDetailsPage.emailVisible
    PrivateKeeperDetailsPage.emailTextBox.value = "acom"
    PrivateKeeperDetailsPage.emailConfirmTextBox.value = "acom"
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the system will display an error for invaild email address \"(.*?)\"$")
  def the_system_will_display_an_error_for_invaild_email_address(errMsg: String)  {
    ErrorPanel.text should include(errMsg) withClue trackingId
  }

  private def enterDobDate() {
    PrivateKeeperDetailsPage.dayDateOfBirthTextBox.value = f"${dob.get(Calendar.DATE)}%02d"  //must be dd format
    PrivateKeeperDetailsPage.monthDateOfBirthTextBox.value = f"${dob.get(Calendar.MONTH) + 1}%02d" //must be mm format
    PrivateKeeperDetailsPage.yearDateOfBirthTextBox.value = dob.get(Calendar.YEAR).toString
  }

}
