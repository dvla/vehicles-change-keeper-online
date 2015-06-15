package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.{VehicleLookupPage, PrivateKeeperDetailsPage, NewKeeperChooseYourAddressPage}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WithClue, WebBrowserDSL, WebBrowserDriver}

class PostCodeValidationTestSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  @When("^the user enters an invalid postcode$")
  def the_user_enters_an_invalid_postcode()  {
    PrivateKeeperDetailsPage.postcodeTextBox enter "rewrewrew"
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the user will see an error message \"(.*?)\"$")
  def the_user_will_see_an_error_message(errMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMsg) shouldBe true
  }

  @Then("^an error message  displays \"(.*?)\"$")
  def an_error_message_displays(g:String) {
  }

  @When("^the user enters an null in  postcode textbox$")
  def the_user_enters_an_null_in_postcode_textbox()  {
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user enters an valid  postcode$")
  def the_user_enters_an_valid_postcode()  {
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "nani"
    PrivateKeeperDetailsPage.lastNameTextBox enter "sree"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99qq"
    click on PrivateKeeperDetailsPage.emailInvisible
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the user will presented with a list of addresses$")
  def the_user_will_presented_with_a_list_of_addresses()  {
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  @When("^the user selects the next button and no errors persist$")
  def the_user_selects_the_next_button_and_no_errors_persist()  {
    the_user_enters_an_valid_postcode()
  }

  @Then("^the user is taken to either postcode lookup success or postcode lookup failure screen$")
  def the_user_is_taken_to_either_postcode_lookup_success_or_postcode_lookup_failure_screen() {
    page.title shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  @When("^the user selects the 'Back' button and no errors persist$")
  def the_user_selects_the_Back_button_and_no_errors_persist()  {
    click on PrivateKeeperDetailsPage.back
  }

  @Then("^the user is taken to the previous page$")
  def the_user_is_taken_to_the_previous_page()  {
     page.title shouldEqual VehicleLookupPage.title withClue trackingId
  }
}
