package gov.uk.dvla.vehicles.keeper.stepdefs

import _root_.helpers.RandomVrmGenerator
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WithClue, WebBrowserDSL, WebBrowserDriver}

class CompleteAndConfirmSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter vrm
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    page.title shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "retail"
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value="0"
    click on NewKeeperChooseYourAddressPage.next
    page.title shouldBe DateOfSalePage.title withClue trackingId
  }

  def goToCompletAndConfirmPage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    goToDateOfSalePage(vrm)
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
    page.title shouldBe CompleteAndConfirmPage.title withClue trackingId
  }

  @Given("^that the user is on the complete and confirm page$")
  def that_the_user_is_on_the_complete_and_confirm_page() {
    goToCompletAndConfirmPage()
  }

  @When("^the user click on confirm sale button$")
  def the_user_click_on_confirm_sale_button()  {
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user is not progressed to the next page$")
  def the_user_is_not_progressed_to_the_next_page(): Unit = {
    page.title shouldEqual CompleteAndConfirmPage.title withClue trackingId
  }

  @When("^the consent field is not checked$")
  def the_consent_field_is_not_checked() {
    if(CompleteAndConfirmPage.consent.isSelected) click on CompleteAndConfirmPage.consent
  }

  @When("^the consent field is checked$")
  def the_consent_field_is_checked() {
//    CompleteAndConfirmPage.dayDateOfSaleTextBox enter "12"
//    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
//    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2010"
    click on CompleteAndConfirmPage.consent
  }

  @Then("^the user is progressed to the next stage of the service$")
  def the_user_is_progressed_to_the_next_stage_of_the_service() {
    page.title shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }
  @Then("^an error message displayed \"(.*?)\"$")
  def an_error_message_displayed(err:String): Unit =  {
    page.source.contains("Transaction Unsuccessful")
  }

  @When("^The user clicks back on Complete and Confirm page$")
  def the_user_clicks_back_on_Complete_and_Confirm_page(): Unit = {
    click on CompleteAndConfirmPage.back
  }

  @When("^The user clicks back on Date of sale page$")
  def the_user_clicks_back_on_Date_of_sale_page(): Unit = {
    click on DateOfSalePage.back
  }
}
