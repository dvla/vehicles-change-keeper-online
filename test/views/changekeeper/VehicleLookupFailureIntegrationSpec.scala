package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import org.openqa.selenium.WebDriver
import pages.changekeeper.{BeforeYouStartPage, VehicleLookupFailurePage, VehicleLookupPage}
import pages.changekeeper.VehicleLookupFailurePage.{beforeYouStart, vehicleLookup}
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.ProgressBar
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts

final class VehicleLookupFailureIntegrationSpec extends UiSpec with TestHarness {
  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      page.title should equal(VehicleLookupFailurePage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "not display any progress indicator when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      page.source should not contain ProgressBar.div
    }

    "redirect to before you start details if cache is empty on page load" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupFailurePage
      page.title should equal(BeforeYouStartPage.title)
    }

    "redirect to before you start page " +
      "if only VehicleLookupFormModelCache is populated" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleLookupFormModel()
      go to VehicleLookupFailurePage
      page.title should equal(BeforeYouStartPage.title)
    }

    "remove redundant cookies when displayed" taggedAs UiTag in new PhantomJsByDefault {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      webDriver.manage().getCookieNamed(VehicleLookupResponseCodeCacheKey) should equal(null)
    }

    "display messages that show that the number of brute force attempts does not impact which messages are displayed " +
      "when 1 attempt has been made" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage

      CookieFactoryForUISpecs.
        bruteForcePreventionViewModel(attempts = 1, maxAttempts = MaxAttempts).
        vehicleLookupFormModel().
        vehicleLookupResponseCode(responseCode = "300L - vehicle_and_keeper_lookup_vrm_not_found")

      go to VehicleLookupFailurePage
      page.source should include("Only a limited number of attempts can be made to retrieve vehicle details " +
        "for each vehicle registration number entered.")
    }

    "display messages that show that the number of brute force attempts does not impact which messages are displayed " +
      "when 2 attempts have been made" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage

      CookieFactoryForUISpecs.
        bruteForcePreventionViewModel(attempts = 2, maxAttempts = MaxAttempts).
        vehicleLookupFormModel().
        vehicleLookupResponseCode(responseCode = "400P - vehicle_and_keeper_lookup_vrm_not_found")

      go to VehicleLookupFailurePage
      page.source should include("Only a limited number of attempts can be made to retrieve vehicle details " +
        "for each vehicle registration number entered.")
    }

    "display appropriate messages for document reference mismatch" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage

      CookieFactoryForUISpecs.
        bruteForcePreventionViewModel().
        vehicleLookupFormModel().
        vehicleLookupResponseCode(responseCode = "600K - vehicle_and_keeper_lookup_document_reference_mismatch")

      go to VehicleLookupFailurePage
      page.source should include("Only a limited number of attempts can be made to retrieve vehicle details " +
        "for each vehicle registration number entered.")
    }
  }

  "vehicleLookup button" should {
    "redirect to vehiclelookup when button clicked" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      click on vehicleLookup
      page.title should equal(VehicleLookupPage.title)
    }
  }

  "beforeYouStart button" should {
    "redirect to beforeyoustart" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to VehicleLookupFailurePage
      click on beforeYouStart
      page.title should equal(BeforeYouStartPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.
      bruteForcePreventionViewModel().
      vehicleLookupFormModel().
      vehicleLookupResponseCode()
}
