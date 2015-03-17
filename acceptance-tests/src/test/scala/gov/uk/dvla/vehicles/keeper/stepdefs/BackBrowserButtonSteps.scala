package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.{VehicleLookupPage, ChangeKeeperSuccessPage, CompleteAndConfirmPage}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class BackBrowserButtonSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]
  lazy val happyPath = new CompleteAndConfirmSteps(webBrowserDriver)

  def goToSuccessfulSummaryPage() = {
    happyPath.goToCompletAndConfirmPage()
    CompleteAndConfirmPage.dayDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2010"
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
