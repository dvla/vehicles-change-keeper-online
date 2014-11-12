package gov.uk.dvla.vehicles.acquire.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import helpers.webbrowser.{WebBrowserDriver, WebBrowserDSL}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._


final class PKeeperTitleFirstLastName(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToPrivateKeeperDetailsPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "B1"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual (PrivateKeeperDetailsPage.title)
  }
  @Given("^that the user selects NO Title option$")
  def  that_the_user_selects_NO_Title_option()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.assertNoTitleSelected()
  }

  @Given("^a valid entry  exists for the title control$")
  def a_valid_entry_exists_for_the_title_control()  {
    goToPrivateKeeperDetailsPage()
    click on PrivateKeeperDetailsPage.mr
  }

  @Then("^the user will be errored \"(.*?)\"$")
  def the_user_will_be_errored(errMessage1:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMessage1) shouldBe  true
  }

  @When("^the user selects the Submit function$")
  def the_user_selects_the_Submit_function()  {
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^user will not see the error \"(.*?)\"$")
  def user_will_not_see_the_error(noErrorMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(noErrorMsg) shouldBe  false
  }

  @Given("^the user selected the title  \"(.*?)\"$")
  def the_user_selected_the_title(title:String) {
    goToPrivateKeeperDetailsPage()
    click on PrivateKeeperDetailsPage.other
  }

  @Given("^the other title text input field is \"(.*?)\"$")
  def the_other_title_text_input_field_is(Null:String) {
    PrivateKeeperDetailsPage.otherText enter ""
  }

  @Then("^an error message will be displayed \"(.*?)\"$")
  def  an_error_message_will_be_displayed(errMessage2:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(errMessage2) shouldBe  true
  }

  @Given("^the user enter null value in firstname textbox$")
  def the_user_enter_null_value_in_firstname_textbox() {
    goToPrivateKeeperDetailsPage()
  }

  @Given("^the user has entered invalid firstName$")
  def the_user_has_entered_invalid_firstName(){
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.firstNameTextBox enter "_@kkgk"
  }

  @Given("^the user has entered valid firstName$")
  def the_user_has_entered_valid_firstName()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.firstNameTextBox enter "nielk"
  }

  @Given("^the user enter null value in lastname textbox$")
  def the_user_enter_null_value_in_lastname_textbox() {
    goToPrivateKeeperDetailsPage()
  }

  @Given("^the user has entered invalid lastName$")
  def the_user_has_entered_invalid_lastName(){
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.firstNameTextBox enter "_@@@@@"
  }

  @Given("^the user has entered valid lastName$")
  def the_user_has_entered_valid_lastName()  {
    goToPrivateKeeperDetailsPage()
    PrivateKeeperDetailsPage.lastNameTextBox enter "nielk"
  }

  @Then("^an error message  should display \"(.*?)\"$")
  def an_error_message_should_display(NameErrMsg:String) {
    PrivateKeeperDetailsPage.errorTextForTitle(NameErrMsg) shouldBe  true
  }

  @Then("^no error message will be displayed \"(.*?)\"$")
  def no_error_message_will_be_displayed(noErrMsg:String)  {
    PrivateKeeperDetailsPage.errorTextForTitle(noErrMsg) shouldBe  false
  }

  @Then("^error message will be display \"(.*?)\"$")
  def  error_message_will_be_display(errMsgFirstName:String){
    PrivateKeeperDetailsPage.errorTextForTitle(errMsgFirstName) shouldBe  true
  }




}
