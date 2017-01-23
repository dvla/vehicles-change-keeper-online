package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import org.openqa.selenium.WebDriver
import pages.changekeeper.MicroServiceErrorPage.exit
import pages.changekeeper.MicroServiceErrorPage.tryAgain
import pages.changekeeper.{BeforeYouStartPage, MicroServiceErrorPage, VehicleLookupPage}
import pages.common.AlternateLanguages.{cymraeg, english}
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.testhelpers.{UiSpec, UiTag}
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

final class MicroServiceErrorIntegrationSpec extends UiSpec with TestHarness {
  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to MicroServiceErrorPage
      pageTitle should equal(MicroServiceErrorPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to MicroServiceErrorPage
      pageSource.contains(EmailFeedbackLink) should equal(true)
    }


  }

  "tryAgain button" should {
    "redirect to vehiclelookup" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to MicroServiceErrorPage
      click on tryAgain
      pageTitle should equal(VehicleLookupPage.title)
    }

    "not be its own referer" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to MicroServiceErrorPage
      click on cymraeg
      click on english
      click on tryAgain
      pageTitle should equal(VehicleLookupPage.title)
    }
  }

  "exit button" should {
    "redirect to beforeyoustart" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to MicroServiceErrorPage
      click on exit
      pageTitle should equal(BeforeYouStartPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.withLanguageEn() // TODO: remove this
}
