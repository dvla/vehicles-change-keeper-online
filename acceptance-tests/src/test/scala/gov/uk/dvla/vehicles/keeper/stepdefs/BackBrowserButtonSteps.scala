package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import helpers.RandomVrmGenerator
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{PhantomJsDefaultDriver, WebBrowserDSL, WebBrowserDriver}

class BackBrowserButtonSteps(implicit webDriver: PhantomJsDefaultDriver)
  extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  def goToDateOfSalePage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter vrm
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

  def goToCompletAndConfirmPage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    goToDateOfSalePage(vrm)
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
    page.title shouldBe CompleteAndConfirmPage.title
  }

  def goToSuccessfulSummaryPage() = {
    goToCompletAndConfirmPage()
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
    page.title shouldEqual ChangeKeeperSuccessPage.title
  }

  @Given("^the user is on the successful summary$")
  def the_user_is_on_the_successful_summary()  {
    goToSuccessfulSummaryPage()
  }

  @When("^the user click on back browser button$")
  def the_user_click_on_back_browser_button()  {
    webDriver.navigate().back()
  }

  @Then("^the user navigate back to the Vehicle look up screen$")
  def the_user_navigate_back_to_the_Vehicle_look_up_screen()  {
    page.title shouldEqual VehicleLookupPage.title
  }

}
