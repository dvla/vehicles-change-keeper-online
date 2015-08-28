package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import helpers.webbrowser.ProgressBar.progressStep
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.{ChangeKeeperSuccessPage, BeforeYouStartPage}
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction

class ChangeKeeperSuccessIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      page.title should equal(ChangeKeeperSuccessPage.title)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      page.source.contains(progressStep(6)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      page.source.contains(progressStep(6)) should equal(false)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "redirect when the cache is missing acquire complete details" taggedAs UiTag in new WebBrowser {
      go to ChangeKeeperSuccessPage
      page.title should equal(BeforeYouStartPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .newKeeperDetailsModel()
      .dateOfSaleDetails()
      .completeAndConfirmDetails()
      .completeAndConfirmResponseModelModel()
}
