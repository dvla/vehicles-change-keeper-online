package views.changekeeper

import helpers.common.ProgressBar
import helpers.tags.UiTag
import helpers.UiSpec
import helpers.webbrowser.{TestGlobal, TestHarness}
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.common.ErrorPanel
import pages.changekeeper.{BeforeYouStartPage, VehicleLookupPage}
import ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import play.api.test.FakeApplication
import pages.changekeeper.VehicleLookupPage.{back, happyPath}
import helpers.changekeeper.CookieFactoryForUISpecs

final class VehicleLookupIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      page.title should equal(VehicleLookupPage.title)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to VehicleLookupPage
      page.source.contains(progressStep(2)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to VehicleLookupPage
      page.source.contains(progressStep(2)) should equal(false)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }
  }

  "next button" should {
    "go to the appropriate next page when private keeper data is entered" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath()
      page.title should equal("Enter the details of the person buying this vehicle")
    }

    "go to the appropriate next page when business keeper data is entered" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(isVehicleSoldToPrivateIndividual = false)
      page.title should equal("Enter the details of the business buying the vehicle")
    }

    "clear businessKeeperDetails when private keeper data is entered" taggedAs UiTag ignore new WebBrowser {
//      ToDo implement test when businessKeeperDetails added
//      go to VehicleLookupPage
//      CookieFactoryForUISpecs.businessKeeperDetails()
//      happyPath()
//      webDriver.manage().getCookieNamed(BusinessKeeperDetailsCacheKey) should equal(null)
    }

    "clear privateKeeperDetails when business keeper data is entered" taggedAs UiTag ignore new WebBrowser {
//      ToDo implement test when privateKeeperDetails added
//      go to BeforeYouStartPage
//      cacheSetup()
//      CookieFactoryForUISpecs.privateKeeperDetails()
//      happyPath(isVehicleSoldToPrivateIndividual = false)
//      webDriver.manage().getCookieNamed(PrivateKeeperDetailsCacheKey) should equal(null)
    }

    "display one validation error message when no referenceNumber is entered" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(referenceNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when no registrationNumber is entered" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a registrationNumber is entered containing one character" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(registrationNumber = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a registrationNumber is entered containing special characters" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(registrationNumber = "$^")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display two validation error messages when no vehicle details are entered but consent is given" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(referenceNumber = "", registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "display one validation error message when only a valid registrationNumber is entered and consent is given" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      happyPath(registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when invalid referenceNumber (Html5Validation disabled)" taggedAs UiTag in new WebBrowser(app = fakeAppWithHtml5ValidationDisabledConfig) {
      go to VehicleLookupPage
      happyPath(referenceNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  "back" should {
    "display previous page when back link is clicked" taggedAs UiTag in new WebBrowser {
      go to VehicleLookupPage
      click on back
      page.title should equal(BeforeYouStartPage.title)
    }
  }

  private val fakeAppWithHtml5ValidationEnabledConfig = FakeApplication(
    withGlobal = Some(TestGlobal),
    additionalConfiguration = Map("html5Validation.enabled" -> true))

  private val fakeAppWithHtml5ValidationDisabledConfig = FakeApplication(
    withGlobal = Some(TestGlobal),
    additionalConfiguration = Map("html5Validation.enabled" -> false))
}