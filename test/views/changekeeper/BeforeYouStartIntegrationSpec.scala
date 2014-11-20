package views.changekeeper

import helpers.common.ProgressBar
import ProgressBar.progressStep
import helpers.tags.UiTag
import helpers.UiSpec
import helpers.webbrowser.TestHarness
import pages.changekeeper.{VehicleLookupPage, BeforeYouStartPage}
import pages.changekeeper.BeforeYouStartPage.startNow
import pages.common.Feedback.EmailFeedbackLink
import helpers.changekeeper.CookieFactoryForUISpecs
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey

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

    "clear all cookies stored in cache" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleLookupFormModel()
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      CookieFactoryForUISpecs.privateKeeperDetails()

      go to BeforeYouStartPage

      webDriver.manage().getCookieNamed(VehicleLookupFormModelCacheKey) should equal(null)
      webDriver.manage().getCookieNamed(VehicleAndKeeperLookupDetailsCacheKey) should equal(null)
      webDriver.manage().getCookieNamed(PrivateKeeperDetailsCacheKey) should equal(null)
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