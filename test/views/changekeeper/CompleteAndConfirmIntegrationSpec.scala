package views.changekeeper

import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.UiSpec
import helpers.webbrowser.ProgressBar
import models.{VehicleNewKeeperCompletionCacheKeys, CompleteAndConfirmFormModel}
import CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.CompleteAndConfirmPage.back
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.VehicleLookupPage
import pages.changekeeper.BeforeYouStartPage
import pages.common.Feedback.EmailFeedbackLink
import ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction

final class CompleteAndConfirmIntegrationSpec extends UiSpec with TestHarness {
  val ProgressStepNumber = 6

  "go to page" should {
    "display the page for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      page.title should equal(CompleteAndConfirmPage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage

      page.source should include(EmailFeedbackLink)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      page.source should include(progressStep(ProgressStepNumber))
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      page.source should not include progressStep(ProgressStepNumber)
    }

    "Redirect when no new keeper details are cached" taggedAs UiTag in new WebBrowser {
      go to CompleteAndConfirmPage
      page.title should equal(VehicleLookupPage.title)
      assertCookiesDoesnExist(Set(AllowGoingToCompleteAndConfirmPageCacheKey))
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
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
      page.title should equal(VehicleLookupPage.title)
      assertCookiesDoesnExist(cookiesDeletedOnRedirect)
    }
  }

  "back" should {
    "display NewKeeperChooseYourAddress when back link is clicked for a new keeper " +
      "who has selected an address" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .privateKeeperDetails()
        .vehicleAndKeeperDetails()
        .newKeeperDetailsModel()
        .dateOfSaleDetails()
        .allowGoingToCompleteAndConfirmPageCookie()

      go to CompleteAndConfirmPage
      click on back
      page.title should equal(DateOfSalePage.title)
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
