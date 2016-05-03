package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.UiSpec
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.BusinessKeeperDetailsPage.{back, navigate}
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import pages.common.ErrorPanel
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import uk.gov.dvla.vehicles.presentation.common.testhelpers.HtmlTestHelper.{htmlRegex, whitespaceRegex}
import org.scalatest.selenium.WebBrowser.{click, currentUrl, go, pageTitle, pageSource}

class BusinessKeeperDetailsIntegrationSpec extends UiSpec with TestHarness {


  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage
      pageTitle should equal(BusinessKeeperDetailsPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage

      pageSource.contains(EmailFeedbackLink) should equal(true)
    }

   "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
      go to BusinessKeeperDetailsPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }

    "display optional for business email input" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to BusinessKeeperDetailsPage

      val pageChars = htmlRegex.replaceAllIn(pageSource, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Contactemailaddress(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(false)
    }
  }

  "next button" should {
    "go to the appropriate next page when all business keeper details are entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate()
      pageTitle should equal (NewKeeperChooseYourAddressPage.title)
    }

    "display one validation error message when an incorrect business name is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(businessName = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect postcode is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(postcode = "Q9")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect email is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(email = "aaa.com")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  "back" should {
    "display previous page when back button is clicked" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()

      go to BusinessKeeperDetailsPage
      click on back
      pageTitle should equal(VehicleLookupPage.title)
      currentUrl should equal(VehicleLookupPage.url)
    }

   "display previous page when back button is clicked with ceg identifier" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails().withIdentifier("CEG")

      go to BusinessKeeperDetailsPage
      click on back
      pageTitle should equal(VehicleLookupPage.title)
      currentUrl should equal(VehicleLookupPage.cegUrl)
    }
 }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}
