package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import models.{VehicleNewKeeperCompletionCacheKeys, CompleteAndConfirmFormModel}
import CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.CompleteAndConfirmPage.back
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.VehicleLookupPage
import pages.changekeeper.BeforeYouStartPage
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

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
      assertCookiesDoesnExist(Set(AllowGoingToCompleteAndConfirmPageCacheKey))
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
      assertCookiesDoesnExist(cookiesDeletedOnRedirect)
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

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .newKeeperDetailsModel()
      .dateOfSaleDetails()
      .allowGoingToCompleteAndConfirmPageCookie()

  private val cookiesDeletedOnRedirect =
    VehicleNewKeeperCompletionCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private def assertCookiesDoesnExist(cookies: Set[String])(implicit driver: WebDriver) =
    for (cookie <- cookies) driver.manage().getCookieNamed(cookie) should be (null)
}
