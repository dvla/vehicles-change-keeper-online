package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class CompleteAndConfirmSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "BF51BOV"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    page.title shouldEqual BusinessKeeperDetailsPage.title
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "retail"
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value="0"
    click on NewKeeperChooseYourAddressPage.next
    page.title shouldBe DateOfSalePage.title
  }

  def goToCompletAndConfirmPage() {
    goToDateOfSalePage()
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
    page.title shouldBe CompleteAndConfirmPage.title
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
    page.title shouldEqual CompleteAndConfirmPage.title
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
    page.title shouldEqual ChangeKeeperSuccessPage.title
  }
  @Then("^an error message displayed \"(.*?)\"$")
  def an_error_message_displayed(err:String): Unit =  {
    page.source.contains("Transaction Unsuccessful")
  }
}

