package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import org.openqa.selenium.WebDriver
import models.HelpCacheKey
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.HelpPage
import pages.changekeeper.HelpPage.{back, exit}
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.common.Feedback.{EmailFeedbackLink, EmailHelpLink}
import pages.common.HelpPanel
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.ProgressBar
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag

final class HelpIntegrationSpec extends UiSpec with TestHarness {
  "go to page" ignore {
    "display the page containing correct title" taggedAs UiTag in new WebBrowser {
      go to HelpPage
      page.title should equal(HelpPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to HelpPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "contain help email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to HelpPage
      page.source.contains(EmailHelpLink) should equal(true)
    }

    "not display any progress indicator when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to HelpPage
      page.source should not contain ProgressBar.div
    }
  }

  "back button" ignore {
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

  "exit" ignore {
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
