package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{click, pageTitle}
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator

class BackBrowserButtonSteps(webBrowserDriver: WebBrowserDriver) extends AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  private val commonSteps = new CommonSteps(webBrowserDriver)

  def goToCompletAndConfirmPage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    commonSteps.goToDateOfSalePage(vrm)
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
