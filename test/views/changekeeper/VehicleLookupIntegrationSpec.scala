package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import models.K2KCacheKeyPrefix.CookiePrefix
import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.support.ui.{ExpectedConditions}
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.selenium.WebBrowser.{go, pageTitle, pageSource, click}
import pages.common.ErrorPanel
import pages.changekeeper.{VehicleLookupPage}
import pages.changekeeper.VehicleLookupPage.happyPath
import uk.gov.dvla.vehicles.presentation.common
import common.filters.CsrfPreventionAction
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.testhelpers.LightFakeApplication
import common.testhelpers.{UiSpec, UiTag}
import common.views.widgetdriver.Wait

class VehicleLookupIntegrationSpec extends UiSpec with TestHarness with Eventually with IntegrationPatience {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      pageTitle should equal(VehicleLookupPage.title)
    }

      "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }

    "display the v5c image on the page with Javascript disabled" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      pageTitle should equal(VehicleLookupPage.title)

      Wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//div[@data-tooltip='tooltip_documentReferenceNumber']")),
        5
      )
    }

    "put the v5c image in a tooltip with Javascript enabled" taggedAs UiTag in new PhantomJsByDefault {
      go to VehicleLookupPage
      val v5c = By.xpath("//div[@data-tooltip='tooltip_documentReferenceNumber']")
      Wait.until(ExpectedConditions.presenceOfElementLocated(v5c), 5)
      Wait.until(ExpectedConditions.invisibilityOfElementLocated(v5c), 5)
    }

    "have a v5c document reference number input field of type tel" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage

      val docRef: WebElement = webDriver.findElement(
        By.name(models.VehicleLookupFormModel.Form.DocumentReferenceNumberId)
      )
      docRef.getAttribute("type") should equal("tel")
    }
  }

  "next button" should {
    "go to the appropriate next page when private keeper data is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath()
      pageTitle should equal("Enter new keeper details")
    }

    "go to the appropriate next page when business keeper data is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(isVehicleSoldToPrivateIndividual = false)
      pageTitle should equal("Enter new keeper details")
    }

    "clear businessKeeperDetails when private keeper data is entered" taggedAs UiTag in new PhantomJsByDefault {
      go to VehicleLookupPage
      CookieFactoryForUISpecs.businessKeeperDetails()
      eventually {
        happyPath()
        webDriver.manage().getCookieNamed(businessKeeperDetailsCacheKey) should equal(null)
      }
    }

    "clear privateKeeperDetails when business keeper data is entered" taggedAs UiTag in new PhantomJsByDefault {
      go to VehicleLookupPage
      CookieFactoryForUISpecs.privateKeeperDetails()
      happyPath(isVehicleSoldToPrivateIndividual = false)
      webDriver.manage().getCookieNamed(privateKeeperDetailsCacheKey) should equal(null)
    }

    "display one validation error message when no referenceNumber is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(referenceNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when no registrationNumber is entered" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message" +
      " when a registrationNumber is entered containing one character" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(registrationNumber = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message " +
      "when a registrationNumber is entered containing special characters" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(registrationNumber = "$^")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display two validation error messages " +
      "when no vehicle details are entered but consent is given" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(referenceNumber = "", registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(2)
    }

    "display one validation error message " +
      "when only a valid registrationNumber is entered and consent is given" taggedAs UiTag in new WebBrowserForSelenium {
      go to VehicleLookupPage
      happyPath(registrationNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message " +
      "when invalid referenceNumber (Html5Validation disabled)" taggedAs UiTag in
      new WebBrowserForSelenium(app = fakeAppWithHtml5ValidationDisabledConfig) {
      go to VehicleLookupPage
      happyPath(referenceNumber = "")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  private val fakeAppWithHtml5ValidationDisabledConfig =
    LightFakeApplication(global, Map("html5Validation.enabled" -> false))
}
