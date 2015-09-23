package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.openqa.selenium.WebDriver
import pages.changekeeper.{VehicleLookupPage, BeforeYouStartPage, VrmLockedPage}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class VrmLockedIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page if appropriate cookies exist" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      successCookiesSetup()
      go to VrmLockedPage
      pageTitle should equal(VrmLockedPage.title)
    }

    "redirect to before you start page if brute force cookie is missing" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to VrmLockedPage
      pageTitle should equal(VehicleLookupPage.title)
      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
    }
  }

  "clicking on the exit button" should {
    "redirect to the before you start page discarding any cookies" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      successCookiesSetup()
      go to VrmLockedPage
      pageTitle should equal(VrmLockedPage.title)
      click on VrmLockedPage.exit

      pageTitle should equal(BeforeYouStartPage.title)
      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
    }
  }

  def successCookiesSetup()(implicit driver: WebDriver): Unit =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .bruteForcePreventionViewModel(
        permitted = false,
        attempts = 4,
        maxAttempts = 3
      )
}
