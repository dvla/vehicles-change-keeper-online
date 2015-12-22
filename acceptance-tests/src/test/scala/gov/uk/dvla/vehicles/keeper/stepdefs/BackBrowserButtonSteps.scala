package gov.uk.dvla.vehicles.keeper.stepdefs

import java.util.Calendar

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDriver}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator

class BackBrowserButtonSteps(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = vrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "retail"
    BusinessKeeperDetailsPage.postcodeField.value =  "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    NewKeeperChooseYourAddressPage.chooseAddress.value=NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
    pageTitle shouldEqual  DateOfSalePage.title withClue trackingId
  }

  def goToCompletAndConfirmPage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    goToDateOfSalePage(vrm)
    DateOfSalePage.dayDateOfSaleTextBox.value =  "12"
    DateOfSalePage.monthDateOfSaleTextBox.value =  "12"
    DateOfSalePage.yearDateOfSaleTextBox.value =  getPreviousYear
    click on DateOfSalePage.next
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
  }

  def goToSuccessfulSummaryPage() = {
    goToCompletAndConfirmPage()
    click on CompleteAndConfirmPage.consent
    click on CompleteAndConfirmPage.next
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
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
    pageTitle shouldEqual VehicleLookupPage.title withClue trackingId
  }

  private def getPreviousYear = {
    (java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)-1).toString
  }

}
