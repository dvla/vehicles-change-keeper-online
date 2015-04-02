package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import pages.changekeeper.{BeforeYouStartPage, CompleteAndConfirmPage, ChangeKeeperSuccessPage}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class SummaryPageSteps (webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebBrowserDriver]
  lazy val happyPath = new CompleteAndConfirmSteps(webBrowserDriver)

  @Given("^the user is on the successful summary page$")
  def the_user_is_on_the_successful_summary_page() {
     happyPath.goToCompletAndConfirmPage("BF51BOV")
     click on CompleteAndConfirmPage.consent
     click on CompleteAndConfirmPage.next
     page.title shouldEqual ChangeKeeperSuccessPage.title
  }

  @Given("^the user can see the Transaction Id Finish and Print button$")
  def the_user_can_see_the_Transaction_Id_Finish_and_Print_button()  {
     page.source should include("BF51BOV-11111111111")
     page.source should include("Finish")
     page.source should include("Print")
  }

  @Given("^the user can see the Thank you message and vehicle details$")
  def the_user_can_see_the_Thank_you_message_and_vehicle_details()  {
    page.source should include("The application is being processed")
    page.source should include("Thank You")
  }

  @Given("^the keeper can see the keeper details$")
  def the_keeper_can_see_the_keeper_details()  {
    page.source should include("RETAIL")
    page.source should include("QQ9 9QQ")
  }

  @When("^the user click on Finish button$")
  def the_user_click_on_Finish_button()  {
    click on ChangeKeeperSuccessPage.finish
  }

  @Then("^the user can navigates to BeforeStartPage$")
  def the_user_can_navigates_to_BeforeStartPage()  {
    page.title shouldEqual BeforeYouStartPage.title
  }
}
