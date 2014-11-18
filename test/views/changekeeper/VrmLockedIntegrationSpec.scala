package views.changekeeper

import helpers.UiSpec
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.tags.UiTag
import helpers.webbrowser.TestHarness
import org.openqa.selenium.WebDriver
import pages.changekeeper.{VehicleLookupPage, BeforeYouStartPage, VrmLockedPage}
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey

class VrmLockedIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page if appropriate cookies exist" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      successCookiesSetup()
      go to VrmLockedPage
      page.title should equal(VrmLockedPage.title)
    }

    "redirect to before you start page if brute force cookie is missing" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to VrmLockedPage
      page.title should equal(VehicleLookupPage.title)
      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
    }
  }

  "clicking on try again button" should {
    "redirect to the vehicles lookup page discarding all the cookies set" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      successCookiesSetup()
      go to VrmLockedPage
      page.title should equal(VrmLockedPage.title)
      click on VrmLockedPage.newDisposal

      page.title should equal(VehicleLookupPage.title)
      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
    }
  }

  "clicking on the exit button" should {
    "redirect to the before you start page discarding any cookies" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      successCookiesSetup()
      go to VrmLockedPage
      page.title should equal(VrmLockedPage.title)
      click on VrmLockedPage.exit

      page.title should equal(BeforeYouStartPage.title)
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
