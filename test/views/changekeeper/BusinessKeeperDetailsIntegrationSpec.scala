package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.webbrowser.ProgressBar
import ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import helpers.UiSpec
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.common.ErrorPanel
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import pages.changekeeper.BusinessKeeperDetailsPage.{back, navigate}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.HtmlTestHelper.{htmlRegex, whitespaceRegex}
import pages.common.Feedback.EmailFeedbackLink

class BusinessKeeperDetailsIntegrationSpec extends UiSpec with TestHarness {

  final val ProgressStepNumber = 3

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage
      page.title should equal(BusinessKeeperDetailsPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage

      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to BusinessKeeperDetailsPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "display optional for business email input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage

      val pageChars = htmlRegex.replaceAllIn(page.source, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Contactemailaddress(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(false)
    }
  }

  "next button" should {
    "go to the appropriate next page when all business keeper details are entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate()
      page.title should equal (NewKeeperChooseYourAddressPage.title)
    }

    "display one validation error message when an incorrect business name is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(businessName = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect postcode is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(postcode = "Q9")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect email is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(email = "aaa.com")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  "back" should {
    "display previous page when back button is clicked" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()

      go to BusinessKeeperDetailsPage
      click on back
      page.title should equal(VehicleLookupPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}
