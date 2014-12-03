package views.changekeeper

import composition.ChangeKeeperTestHarness
import helpers.webbrowser.ProgressBar
import helpers.UiSpec
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.tags.UiTag
import org.openqa.selenium.WebDriver
import pages.changekeeper.MicroServiceErrorPage.{exit, tryAgain}
import pages.changekeeper.{BeforeYouStartPage, MicroServiceErrorPage}
import pages.common.Feedback.EmailFeedbackLink

final class MicroServiceErrorIntegrationSpec extends UiSpec with ChangeKeeperTestHarness {
  "go to page" should {
    // TODO: fix me
    "display the page" taggedAs UiTag ignore new WebBrowser {
      go to MicroServiceErrorPage
      page.title should equal(MicroServiceErrorPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag ignore new WebBrowser {
      go to MicroServiceErrorPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "not display any progress indicator when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to MicroServiceErrorPage
      page.source should not contain ProgressBar.div
    }
  }

  "tryAgain button" should {
    // TODO fix the ignored tests when the story is ready
    "redirect to vehiclelookup" taggedAs UiTag ignore new WebBrowser {
      /*
      go to BeforeYouStartPage
      cacheSetup()
      go to MicroServiceErrorPage
      click on tryAgain
      page.title should equal(VehicleLookupPage.title)
      */
    }

    "redirect to setuptradedetails when no details are cached" taggedAs UiTag ignore new WebBrowser {
      /*
      go to MicroServiceErrorPage
      click on tryAgain
      page.title should equal(SetupTradeDetailsPage.title)
      */
    }
  }

  "exit button" should {
    "redirect to beforeyoustart" taggedAs UiTag ignore new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to MicroServiceErrorPage
      click on exit
      page.title should equal(BeforeYouStartPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.withLanguageEn() // TODO: remove this
//      setupTradeDetails().
//      dealerDetails()
}
