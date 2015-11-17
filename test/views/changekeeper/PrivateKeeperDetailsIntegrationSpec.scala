package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import helpers.webbrowser.ProgressBar.progressStep
import org.openqa.selenium.{By, WebDriver, WebElement}
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.PrivateKeeperDetailsPage.{back, navigate}
import pages.changekeeper.PrivateKeeperDetailsPage.{DriverNumberInvalid, EmailInvalid}
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameInvalid, LastNameInvalid}
import pages.changekeeper.PrivateKeeperDetailsPage.{TitleInvalid,  PostcodeInvalid}
import pages.changekeeper.VehicleLookupPage
import pages.common.ErrorPanel
import pages.common.Feedback.EmailFeedbackLink
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import uk.gov.dvla.vehicles.presentation.common.testhelpers.HtmlTestHelper.{htmlRegex, whitespaceRegex}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import org.scalatest.selenium.WebBrowser.{click, currentUrl, go, pageTitle, pageSource}

final class PrivateKeeperDetailsIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      pageTitle should equal(PrivateKeeperDetailsPage.title)

      PrivateKeeperDetailsPage.yearDateOfBirthTextBox.text should equal("")
      PrivateKeeperDetailsPage.monthDateOfBirthTextBox.text should equal("")
      PrivateKeeperDetailsPage.dayDateOfBirthTextBox.text should equal("")
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      pageSource.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      pageSource.contains(progressStep(3)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      pageSource.contains(progressStep(3)) should equal(false)
    }

    "Redirect when no vehicle details are cached" taggedAs UiTag in new WebBrowserForSelenium {
      go to PrivateKeeperDetailsPage
      pageTitle should equal(VehicleLookupPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
      go to PrivateKeeperDetailsPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }

    "display optional for driving licence number of new keeper input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage

      val pageChars = htmlRegex.replaceAllIn(pageSource, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Drivinglicencenumberofnewkeeper(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
    }

    "display optional for date of birth input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage

      val pageChars = htmlRegex.replaceAllIn(pageSource, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Dateofbirth(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
    }

    "display optional for email address input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage

      val pageChars = htmlRegex.replaceAllIn(pageSource, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Emailaddress(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(false)
    }
  }

  "next button" should {
    "go to the appropriate next page when all private keeper details are entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate()
      pageTitle should equal("Select new keeper address")
    }

    "go to the appropriate next page " +
      "when mandatory private keeper details are entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(email = "avalid@email.address")
      pageTitle should equal("Select new keeper address")
    }

    "display one validation error message when no title is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(title = TitleInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect email is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(email = EmailInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect driver number is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(driverNumber = DriverNumberInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when no firstname is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(firstName = FirstNameInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when no lastName is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(lastName = LastNameInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when an incorrect postcode is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(postcode = PostcodeInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message " +
      "when the title and the first name are longer then 26" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(title = "tenchartdd", firstName = "15characterssdyff")
      ErrorPanel.text should include(Messages("error.titlePlusFirstName.tooLong"))
    }

    "convert lower case driver number to upper case" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(driverNumber = "abcd9711215eflgh")
      click on NewKeeperChooseYourAddressPage.back
      pageSource should include("ABCD9711215EFLGH")
    }
  }

  "back" should {
    "display previous page when back link is clicked" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()

      go to PrivateKeeperDetailsPage
      click on back
      pageTitle should equal(VehicleLookupPage.title)
      currentUrl should equal(VehicleLookupPage.url)
    }

    "display previous page when back link is clicked with ceg identifier" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup().withIdentifier("CEG")

      go to PrivateKeeperDetailsPage
      click on back
      pageTitle should equal(VehicleLookupPage.title)
      currentUrl should equal(VehicleLookupPage.cegUrl)
    }

  }

  private def cacheSetup()(implicit webDriver: WebDriver) = CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}
