package views.changekeeper

import composition.TestHarness
import helpers.{CookieFactoryForUISpecs, UiSpec}
import org.openqa.selenium.{By, WebElement, WebDriver}
import org.scalatest.selenium.WebBrowser.{click, currentUrl, go}
import pages.changekeeper.{BeforeYouStartPage, VrmLockedPage}
import pages.changekeeper.VrmLockedPage.exit
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag

class VrmLockedUiSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup
      go to VrmLockedPage

      currentUrl should equal(VrmLockedPage.url)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
      go to VrmLockedPage
      val csrf: WebElement = webDriver.findElement(
        By.name(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      )
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").length > 0 should equal(true)
    }

    "contain the time of locking" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup
      go to VrmLockedPage
      val localTime: WebElement = webDriver.findElement(By.id("localTimeOfVrmLock"))
      localTime.isDisplayed should equal(true)
      localTime.getText should include regex "^(\\d|0\\d|1\\d|2[0-3]):[0-5]\\d".r
    }

    "contain the time of locking when JavaScript is disabled" taggedAs UiTag in new WebBrowserWithJsDisabled {
      go to BeforeYouStartPage
      cacheSetup
      go to VrmLockedPage
      val localTime: WebElement = webDriver.findElement(By.id("localTimeOfVrmLock"))
      localTime.isDisplayed should equal(true)
      localTime.getText should include regex "^(\\d|0\\d|1\\d|2[0-3]):[0-5]\\d".r
    }
  }

  "exit button" should {
    "redirect to feedback page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to VrmLockedPage
      click on exit
      currentUrl should equal(BeforeYouStartPage.url)
    }
  }

  def cacheSetup()(implicit driver: WebDriver): Unit =
    CookieFactoryForUISpecs.
      vehicleAndKeeperDetails().
      bruteForcePreventionViewModel()
}
