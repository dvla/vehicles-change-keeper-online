package views.changekeeper

import helpers.UiSpec
import helpers.common.ProgressBar
import helpers.tags.UiTag
import helpers.webbrowser.TestHarness
import org.openqa.selenium.WebDriver
import pages.changekeeper.HelpPage.{back, exit}
import pages.common.HelpPanel
import models.HelpCacheKey
import helpers.changekeeper.CookieFactoryForUISpecs
import pages.changekeeper.{PrivateKeeperDetailsPage, VehicleLookupPage, HelpPage, BeforeYouStartPage}
import pages.common.Feedback.EmailFeedbackLink

final class HelpIntegrationSpec extends UiSpec with TestHarness {
  "go to page" should {
    "display the page containing correct title" taggedAs UiTag in new WebBrowser {
      go to HelpPage
      page.title should equal(HelpPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to HelpPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "not display any progress indicator when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to HelpPage
      page.source should not contain ProgressBar.div
    }
  }

  "back button" should {
    "redirect to the users previous page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      click on HelpPanel.help
      click on back
      page.title should equal(PrivateKeeperDetailsPage.title)
    }

    "remove cookie" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      click on HelpPanel.help
      click on back
      webDriver.manage().getCookieNamed(HelpCacheKey) should equal(null)
    }
  }

  "exit" should {
    "redirect to the start page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to PrivateKeeperDetailsPage
      click on HelpPanel.help
      click on exit
      page.title should equal(BeforeYouStartPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) = CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}