package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import pages.changekeeper.{BeforeYouStartPage, CompleteAndConfirmPage, ChangeKeeperSuccessPage}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, pageTitle, pageSource}

class SummaryPageSteps (webBrowserDriver: WebBrowserDriver)
  extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebBrowserDriver]
  lazy val happyPath = new CompleteAndConfirmSteps(webBrowserDriver)

  private val vrm = RandomVrmGenerator.uniqueVrm

  @Given("^the user is on the successful summary page$")
  def the_user_is_on_the_successful_summary_page() {
     happyPath.goToCompletAndConfirmPage(vrm)
     click on CompleteAndConfirmPage.consent
     click on CompleteAndConfirmPage.regRight
     click on CompleteAndConfirmPage.next
     pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }

  @Given("^the user can see the Transaction Id Finish and Print button$")
  def the_user_can_see_the_Transaction_Id_Finish_and_Print_button()  {
     pageSource should include(s"${vrm}-11111111111") withClue trackingId
     pageSource should include("Finish") withClue trackingId
     pageSource should include("Print") withClue trackingId
  }

  @Given("^the user can see the Thank you message and vehicle details$")
  def the_user_can_see_the_Thank_you_message_and_vehicle_details()  {
    pageSource should include("The application is being processed") withClue trackingId
    pageSource should include("Thank you") withClue trackingId
  }

  @Given("^the keeper can see the keeper details$")
  def the_keeper_can_see_the_keeper_details()  {
    pageSource should include("RETAIL") withClue trackingId
    pageSource should include("QQ9 9QQ") withClue trackingId
  }
}
