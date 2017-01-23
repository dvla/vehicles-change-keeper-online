package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.{ChangeKeeperSuccessPage, BeforeYouStartPage}
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.testhelpers.{UiSpec, UiTag}
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import org.scalatest.selenium.WebBrowser.{go, pageTitle, pageSource}

class ChangeKeeperSuccessIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      pageTitle should equal(ChangeKeeperSuccessPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to ChangeKeeperSuccessPage
      pageSource.contains(EmailFeedbackLink) should equal(true)
    }

    "redirect when the cache is missing acquire complete details" taggedAs UiTag in new WebBrowserForSelenium {
      go to ChangeKeeperSuccessPage
      pageTitle should equal(BeforeYouStartPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
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
