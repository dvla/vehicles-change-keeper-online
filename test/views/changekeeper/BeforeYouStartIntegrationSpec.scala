package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.UiSpec
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BeforeYouStartPage.startNow
import pages.changekeeper.VehicleLookupPage
import pages.common.AlternateLanguages._
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import common.testhelpers.UiTag
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class BeforeYouStartIntegrationSpec extends UiSpec with TestHarness {


  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      pageTitle should equal(BeforeYouStartPage.title)
    }

    //TODO uncomment when Welsh translation complete
/*
    "display the 'Cymraeg' language button and not the 'English' language button when the play language cookie has " +
      "value 'en'" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage // By default will load in English.
      CookieFactoryForUISpecs.withLanguageEn()
      go to BeforeYouStartPage

      isCymraegDisplayed should equal(true)
      isEnglishDisplayed should equal(false)
    }

    "display the 'English' language button and not the 'Cymraeg' language button when the play language cookie has "  +
      "value 'cy'" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage // By default will load in English.
      CookieFactoryForUISpecs.withLanguageCy()
      go to BeforeYouStartPage

      isCymraegDisplayed should equal(false)
      isEnglishDisplayed should equal(true)
      pageTitle should equal(BeforeYouStartPage.titleCy)
    }

    "display the 'Cymraeg' language button and not the 'English' language button and mailto when the play language " +
      "cookie does not exist (assumption that the browser default language is English)" taggedAs UiTag in
      new WebBrowserForSelenium {
      go to BeforeYouStartPage

      isCymraegDisplayed should equal(true)
      isEnglishDisplayed should equal(false)
    }
*/

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      pageSource.contains(EmailFeedbackLink) should equal(true)
    }

    "clear all cookies stored in cache" taggedAs UiTag in new PhantomJsByDefault {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleLookupFormModel()
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      CookieFactoryForUISpecs.privateKeeperDetails()

      go to BeforeYouStartPage

      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
      webDriver.manage().getCookieNamed(vehicleAndKeeperLookupDetailsCacheKey) should equal(null)
      webDriver.manage().getCookieNamed(privateKeeperDetailsCacheKey) should equal(null)
    }
  }

  "startNow button" should {
    "go to next page" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      click on startNow
      pageTitle should equal(VehicleLookupPage.title)
    }
  }
}
