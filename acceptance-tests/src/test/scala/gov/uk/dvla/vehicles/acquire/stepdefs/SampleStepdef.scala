package gov.uk.dvla.vehicles.acquire.stepdefs
import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDriver, WebBrowserDSL}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._



  final class SampleStepdef(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

    implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToSetupTradeDetailsPage() = {
    go to BeforeYouStartPage
    page.title should equal(BeforeYouStartPage.title)
    //click on BeforeYouStartPage.startNow

  }

  @Given("^the user is on the Provide trader details page$")
  def  the_user_is_on_the_Provide_trader_details_page(){

  }
  @Given("^that a customer has accessed the keeper to keeper service$")
  def that_a_customer_has_accessed_the_keeper_to_keeper_service() {
    goToSetupTradeDetailsPage()

  }

  @When("^they select the 'Next' control button on the before you start page$")
  def  they_select_the_Next_control_button_on_the_before_you_start_page()  {
    click on BeforeYouStartPage.startNow
  }

  @Then("^they will proceed to the 'Vehicle and Keeper' look-up page$")
  def  they_will_proceed_to_the_Vehicle_and_Keeper_look_up_page()  {

    page.title should equal (VehicleLookupPage.title)

  }

  @Then("^the customer will be able to input a VRN, a Doc ref number$")
  def  the_customer_will_be_able_to_input_a_VRN_a_Doc_ref_number() {
        VehicleLookupPage.documentReferenceNumber enter "A1"
  }


  @Then("^the customer will be able to select either 'Private Individual' or 'Business'$")
  def  the_customer_will_be_able_to_select_either_Private_Individual_or_Business()  {
        click on VehicleLookupPage.vehicleSoldToPrivateIndividual
  }

  @Then("^the customer will be presented with 'Next' and 'Back' control buttons/Links$")
  def the_customer_will_be_presented_with_Next_and_Back_control_buttons_Links()  {

    VehicleLookupPage.next.text shouldEqual ("Next")
    VehicleLookupPage.back.text shouldEqual("Back")


  }

}
