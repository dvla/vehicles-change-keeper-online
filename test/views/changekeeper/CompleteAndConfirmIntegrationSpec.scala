package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import models.{CompleteAndConfirmFormModel, VehicleNewKeeperCompletionCacheKeys}
import CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.scalatest.selenium.WebBrowser.{click, go, pageSource, pageTitle}
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.CompleteAndConfirmPage.back
import pages.changekeeper.CompleteAndConfirmPage.navigate
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.MicroServiceErrorPage
import pages.changekeeper.VehicleLookupPage
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common
import common.webserviceclients.fakes.FakeAcquireWebServiceImpl.SimulateForbidden
import common.webserviceclients.fakes.FakeAcquireWebServiceImpl.SimulateMicroServiceUnavailable
import common.webserviceclients.fakes.FakeAcquireWebServiceImpl.SimulateSoapEndpointFailure
import common.filters.CsrfPreventionAction
import common.testhelpers.{UiSpec, UiTag}

final class CompleteAndConfirmIntegrationSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page for a new keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      pageTitle should equal(CompleteAndConfirmPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage

      pageSource should include(EmailFeedbackLink)
    }

    "Redirect when no new keeper details are cached" taggedAs UiTag in new WebBrowserForSelenium {
      go to CompleteAndConfirmPage
      pageTitle should equal(VehicleLookupPage.title)
      assertCookiesDontExist(Set(AllowGoingToCompleteAndConfirmPageCacheKey))
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowserForSelenium {
      go to CompleteAndConfirmPage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }

    "redirect to vehicles lookup page " +
      "if there is no cookie preventGoingToCompleteAndConfirmPage set" taggedAs UiTag in new PhantomJsByDefault {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .vehicleAndKeeperDetails()
        .newKeeperDetailsModel()
      go to CompleteAndConfirmPage
      pageTitle should equal(VehicleLookupPage.title)
      assertCookiesDontExist(cookiesDeletedOnRedirect)
    }
  }

  "submit button" should {
    "go to the appropriate next page when all details are entered for a change keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup()
      navigate()
      pageTitle should equal(ChangeKeeperSuccessPage.title)
    }

    "go to the success page when acquire fulfil returns 403 FORBIDDEN" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup(docReferenceNumber = SimulateForbidden)
      navigate()
      pageTitle should equal(ChangeKeeperSuccessPage.title)
    }

    "go to the microservice error page when acquire fulfil is unavailable" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup(docReferenceNumber = SimulateMicroServiceUnavailable)
      navigate()
      pageTitle should equal(MicroServiceErrorPage.title)
    }

    "go to the microservice error page when soap end point is down" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetup(docReferenceNumber = SimulateSoapEndpointFailure)
      navigate()
      pageTitle should equal(MicroServiceErrorPage.title)
    }
  }

  "back" should {
    "display NewKeeperChooseYourAddress when back link is clicked for a new keeper " +
      "who has selected an address" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .privateKeeperDetails()
        .vehicleAndKeeperDetails()
        .newKeeperDetailsModel()
        .dateOfSaleDetails()
        .allowGoingToCompleteAndConfirmPageCookie()

      go to CompleteAndConfirmPage
      click on back
      pageTitle should equal(DateOfSalePage.title)
    }
  }

  private def cacheSetup(docReferenceNumber: String)(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleLookupFormModel(referenceNumber = docReferenceNumber)
      .sellerEmailModel()
      .vehicleAndKeeperDetails()
      .newKeeperDetailsModel()
      .dateOfSaleDetails()
      .allowGoingToCompleteAndConfirmPageCookie()

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleLookupFormModel()
      .sellerEmailModel()
      .vehicleAndKeeperDetails()
      .newKeeperDetailsModel()
      .dateOfSaleDetails()
      .allowGoingToCompleteAndConfirmPageCookie()

  private val cookiesDeletedOnRedirect =
    VehicleNewKeeperCompletionCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private def assertCookiesDontExist(cookies: Set[String])(implicit driver: WebDriver) =
    for (cookie <- cookies) driver.manage().getCookieNamed(cookie) should be (null)
}
