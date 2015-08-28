package views.changekeeper

import composition.TestHarness
import helpers.UiSpec
import helpers.webbrowser.ProgressBar.progressStep
import org.openqa.selenium.{By, WebDriver, WebElement}
import pages.changekeeper.{BeforeYouStartPage, NewKeeperEnterAddressManuallyPage}
import pages.changekeeper.NewKeeperEnterAddressManuallyPage.{sadPath, happyPath, happyPathMandatoryFieldsOnly}
import pages.common.ErrorPanel
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import helpers.CookieFactoryForUISpecs

class NewKeeperEnterAddressManuallyIntegrationSpec extends UiSpec with TestHarness {

  final val ProgressStepNumber = 4

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to NewKeeperEnterAddressManuallyPage
      page.title should equal(NewKeeperEnterAddressManuallyPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to NewKeeperEnterAddressManuallyPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to NewKeeperEnterAddressManuallyPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to NewKeeperEnterAddressManuallyPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to NewKeeperEnterAddressManuallyPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }
  }

  "next button" should {
    "accept and redirect when all fields are input with valid entry" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      happyPath()
      page.title should equal("Sale details")
    }

    "accept when only mandatory fields only are input" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      happyPathMandatoryFieldsOnly()
      page.title should equal("Sale details")
    }

    "display validation error messages when no details are entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      sadPath
      ErrorPanel.numberOfErrors should equal(2)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .businessKeeperDetails() // Not bothering with private keeper details
}
