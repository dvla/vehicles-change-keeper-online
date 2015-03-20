package views.changekeeper

import composition.TestHarness
import helpers.UiSpec
import models.CompleteAndConfirmFormModel._
import org.openqa.selenium.{By, WebElement}
import pages.changekeeper.{VehicleLookupPage, CompleteAndConfirmPage}
import pages.common.Feedback._
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.ProgressBar._
import com.google.inject.Injector
import com.tzavellas.sse.guice.ScalaModule
import composition.{TestHarness, GlobalLike, TestComposition}
import helpers.CookieFactoryForUISpecs
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import helpers.UiSpec
import models.{VehicleNewKeeperCompletionCacheKeys, CompleteAndConfirmFormModel}
import org.openqa.selenium.{By, WebElement, WebDriver}
import org.scalatest.mock.MockitoSugar
import pages.common.ErrorPanel
import pages.changekeeper._
import play.api.i18n.Messages
import play.api.libs.ws.WSResponse
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import webserviceclients.fakes.FakeAddressLookupService.addressWithUprn
import pages.changekeeper.DateOfSalePage.navigate
import pages.changekeeper.CompleteAndConfirmPage.back
import pages.changekeeper.DateOfSalePage.mileageTextBox
import pages.changekeeper.DateOfSalePage.dayDateOfSaleTextBox
import pages.changekeeper.DateOfSalePage.monthDateOfSaleTextBox
import pages.changekeeper.DateOfSalePage.yearDateOfSaleTextBox
import pages.changekeeper.CompleteAndConfirmPage.next
import pages.changekeeper.CompleteAndConfirmPage.consent
import pages.changekeeper.DateOfSalePage.useTodaysDate
import webserviceclients.fakes.FakeDateServiceImpl.DateOfAcquisitionDayValid
import webserviceclients.fakes.FakeDateServiceImpl.DateOfAcquisitionMonthValid
import webserviceclients.fakes.FakeDateServiceImpl.DateOfAcquisitionYearValid
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import scala.concurrent.Future
import play.api.test.FakeApplication
import uk.gov.dvla.vehicles.presentation.common.testhelpers.HtmlTestHelper.{htmlRegex, whitespaceRegex}
import scala.Some
import play.api.test.FakeApplication
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType


class DateOfSaleIntegrationSpec extends UiSpec with TestHarness {
  private final val ProgressStepNumber = 5

  "go to page" should {
    "display the page for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.title should equal(DateOfSalePage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage

      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
    }

    "Redirect when no new keeper details are cached" taggedAs UiTag in new WebBrowser {
      go to DateOfSalePage
      page.title should equal(VehicleLookupPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to DateOfSalePage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "display optional for vehicle mileage input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage

      val pageChars = htmlRegex.replaceAllIn(page.source, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Vehiclemileage(optional)"

      pageCharsNoWhitespace should include(optionalLabelValue)
    }
  }

  "Submit button" should {

    "display one validation error message when a mileage is entered greater than max length for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "1000000")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a mileage is entered less than min length for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "-1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a mileage containing letters is entered for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when day date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(dayDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when month date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(monthDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when year date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(yearDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when day date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(dayDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when month date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(monthDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when year date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(yearDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.vehicleAndKeeperDetails()
}
