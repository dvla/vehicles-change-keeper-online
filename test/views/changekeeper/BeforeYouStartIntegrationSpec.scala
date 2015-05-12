package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.webbrowser.ProgressBar.progressStep
import helpers.UiSpec
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import pages.changekeeper.BeforeYouStartPage.startNow
import pages.changekeeper.{VehicleLookupPage, BeforeYouStartPage}
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.testhelpers.UiTag

class BeforeYouStartIntegrationSpec extends UiSpec with TestHarness {
  final val ProgressStepNumber = 1

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      page.title should equal(BeforeYouStartPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
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
    "go to next page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      click on startNow
      page.title should equal(VehicleLookupPage.title)
    }
  }
}
