package views.changekeeper

import helpers.common.ProgressBar
import ProgressBar.progressStep
import helpers.tags.UiTag
import helpers.UiSpec
import helpers.webbrowser.TestHarness
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.common.ErrorPanel
import play.api.i18n.Messages
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import pages.changekeeper.PrivateKeeperDetailsPage.{navigate, back}
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameInvalid, LastNameInvalid, TitleInvalid}
import pages.changekeeper.PrivateKeeperDetailsPage.DriverNumberInvalid
import pages.common.Feedback.AcquireEmailFeedbackLink
import helpers.common.HtmlTestHelper.{htmlRegex, whitespaceRegex}
import pages.changekeeper.{VehicleLookupPage, PrivateKeeperDetailsPage, BeforeYouStartPage}
import helpers.changekeeper.CookieFactoryForUISpecs
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid

final class PrivateKeeperDetailsIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      page.title should equal(PrivateKeeperDetailsPage.title)

//      ToDo uncomment three lines below when date of birth is implemented
//      PrivateKeeperDetailsPage.yearDateOfBirthTextBox.text should equal("")
//      PrivateKeeperDetailsPage.monthDateOfBirthTextBox.text should equal("")
//      PrivateKeeperDetailsPage.dayDateOfBirthTextBox.text should equal("")
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      page.source.contains(AcquireEmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      page.source.contains(progressStep(3)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      page.source.contains(progressStep(3)) should equal(false)
    }

    "Redirect when no vehicle details are cached" taggedAs UiTag in new WebBrowser {
      go to PrivateKeeperDetailsPage
      page.title should equal(VehicleLookupPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to PrivateKeeperDetailsPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

//    ToDo uncomment when driver number is implemented
//    "display optional for driving license number of new keeper input" taggedAs UiTag in new ProgressBarFalse {
//      go to BeforeYouStartPage
//      cacheSetup()
//      go to PrivateKeeperDetailsPage
//
//      val pageChars = htmlRegex.replaceAllIn(page.source, "")
//      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
//      val optionalLabelValue = "Drivinglicensenumberofnewkeeper(optional)"
//
//      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
//    }

//    ToDo uncomment when date of birth is implemented
//    "display optional for date of birth input" taggedAs UiTag in new ProgressBarFalse {
//      go to BeforeYouStartPage
//      cacheSetup()
//      go to PrivateKeeperDetailsPage
//
//      val pageChars = htmlRegex.replaceAllIn(page.source, "")
//      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
//      val optionalLabelValue = "Dateofbirthofnewkeeper(optional)"
//
//      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
//    }

//    ToDo uncomment when email address is implemented
//    "display optional for email address input" taggedAs UiTag in new ProgressBarFalse {
//      go to BeforeYouStartPage
//      cacheSetup()
//      go to PrivateKeeperDetailsPage
//
//      val pageChars = htmlRegex.replaceAllIn(page.source, "")
//      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
//      val optionalLabelValue = "Emailaddress(optional)"
//
//      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
//    }
  }

  "next button" should {
    "go to the appropriate next page when all private keeper details are entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate()
      page.source should equal("success") //ToDo amend test to check page title when next step is implemented
    }

    "go to the appropriate next page when mandatory private keeper details are entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(email = "")
      page.source should equal("success") //ToDo amend test to check page title when next step is implemented
    }

    "display one validation error message when no title is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(title = TitleInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

//    ToDo uncomment test when email is implemented
//    "display one validation error message when an incorrect email is entered" taggedAs UiTag in new WebBrowser {
//      go to BeforeYouStartPage
//      cacheSetup()
//      navigate(email = EmailInvalid)
//      ErrorPanel.numberOfErrors should equal(1)
//    }
//
//    ToDo uncomment test when driver number is implemented
//    "display one validation error message when an incorrect driver number is entered" taggedAs UiTag in new WebBrowser {
//      go to BeforeYouStartPage
//      cacheSetup()
//      navigate(driverNumber = DriverNumberInvalid)
//      ErrorPanel.numberOfErrors should equal(1)
//    }

    "display one validation error message when no firstname is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(firstName = FirstNameInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when no lastName is entered" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(lastName = LastNameInvalid)
      ErrorPanel.numberOfErrors should equal(1)
    }

//    ToDo uncomment test when postcode is implemented
//    "display one validation error message when an incorrect postcode is entered" taggedAs UiTag in new WebBrowser {
//      go to BeforeYouStartPage
//      cacheSetup()
//      navigate(postcode = PostcodeInvalid)
//      ErrorPanel.numberOfErrors should equal(1)
//    }

    "display one validation error message when the title and the first name are longer then 26" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(title = "tenchartdd", firstName = "15characterssdyff")
      ErrorPanel.text should include(Messages("error.titlePlusFirstName.tooLong"))
    }
  }

  "back" should {
    "display previous page when back link is clicked with " taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()

      go to PrivateKeeperDetailsPage
      click on back
      page.title should equal(VehicleLookupPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) = CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}