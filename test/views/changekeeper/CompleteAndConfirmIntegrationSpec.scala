package views.changekeeper

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
import pages.changekeeper.CompleteAndConfirmPage.navigate
import pages.changekeeper.CompleteAndConfirmPage.back
import pages.changekeeper.CompleteAndConfirmPage.mileageTextBox
import pages.changekeeper.CompleteAndConfirmPage.dayDateOfSaleTextBox
import pages.changekeeper.CompleteAndConfirmPage.monthDateOfSaleTextBox
import pages.changekeeper.CompleteAndConfirmPage.yearDateOfSaleTextBox
import pages.changekeeper.CompleteAndConfirmPage.next
import pages.changekeeper.CompleteAndConfirmPage.consent
import pages.changekeeper.CompleteAndConfirmPage.useTodaysDate
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

final class CompleteAndConfirmIntegrationSpec extends UiSpec with TestHarness {
  final val ProgressStepNumber = 5

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

      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
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
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "redirect to vehicles lookup page if there is no cookie preventGoingToCompleteAndConfirmPage set" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .vehicleAndKeeperDetails()
        .newKeeperDetailsModel()
      go to CompleteAndConfirmPage
      page.title should equal(VehicleLookupPage.title)
      assertCookiesDoesnExist(cookiesDeletedOnRedirect)
    }

    "display optional for vehicle mileage input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage

      val pageChars = htmlRegex.replaceAllIn(page.source, "")
      val pageCharsNoWhitespace = whitespaceRegex.replaceAllIn(pageChars, "")
      val optionalLabelValue = "Vehiclemileage(optional)"

      pageCharsNoWhitespace.contains(optionalLabelValue) should equal(true)
    }
  }

  "submit button" should {
    //    "go to the appropriate next page when all details are entered for a new keeper" taggedAs UiTag in new WebBrowser {
    // ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      navigate()
    //      page.title should equal(AcquireSuccessPage.title)
    //    }

    //    "go to the AcquireFailure page when all details are entered for a new keeper" taggedAs UiTag in new MockAppWebBrowser(failingWebService) {
    // ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      navigate()
    //      page.title should equal(Messages("error.title"))
    //    }

    //    "go to the appropriate next page when mandatory details are entered for a new keeper" taggedAs UiTag in new WebBrowser {
    //ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      navigate(mileage = "")
    //      page.title should equal(AcquireSuccessPage.title)
    //    }

    //    "clear off the preventGoingToCompleteAndConfirmPage cookie on success" taggedAs UiTag in new WebBrowser {
    //ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      assertCookieExist
    //      navigate()
    //      page.title should equal(AcquireSuccessPage.title)
    //      assertCookiesDoesnExist(Set(AllowGoingToCompleteAndConfirmPageCacheKey))
    //    }

    //    "clear off the preventGoingToCompleteAndConfirmPage cookie on failure" taggedAs UiTag in new MockAppWebBrowser(failingWebService) {
    //ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      assertCookieExist
    //      navigate()
    //      page.title should equal(Messages("error.title"))
    //      assertCookiesDoesnExist(Set(AllowGoingToCompleteAndConfirmPageCacheKey))
    //    }

    /* Only works with phantomjs, chrome and firefox. Commenting out as not working with htmlunit

import org.openqa.selenium.JavascriptExecutor
import composition.{TestComposition, GlobalLike}
import helpers.webbrowser.{TestGlobal, WebDriverFactory}
import com.google.inject.Injector
import com.tzavellas.sse.guice.ScalaModule
import org.openqa.selenium.interactions.Actions
import org.scalatest.concurrent.Eventually
import org.scalatest.mock.MockitoSugar
import pages.acquire.CompleteAndConfirmPage.next
import pages.acquire.CompleteAndConfirmPage.mileageTextBox
import pages.acquire.CompleteAndConfirmPage.consent
import scala.concurrent.Future
import webserviceclients.acquire.{AcquireRequestDto, AcquireWebService}
import webserviceclients.fakes.FakeAcquireWebServiceImpl
import play.api.libs.ws.WSResponse
import play.api.test.FakeApplication

    val countingWebService = new FakeAcquireWebServiceImpl {
      var calls = List[(AcquireRequestDto, String)]()

      override def callAcquireService(request: AcquireRequestDto, trackingId: String): Future[WSResponse] = {
        calls ++= List(request -> trackingId)
        super.callAcquireService(request, trackingId)
      }
    }

    object MockAcquireServiceCompositionGlobal extends GlobalLike with TestComposition {
      override lazy val injector: Injector = TestGlobal.testInjector(new ScalaModule with MockitoSugar {
        override def configure() {
          bind[AcquireWebService].toInstance(countingWebService)
        }
      })
    }

    "be disabled after click" taggedAs UiTag in new WebBrowser(
      app = FakeApplication(withGlobal = Some(MockAcquireServiceCompositionGlobal)),
      webDriver = WebDriverFactory.webDriver(javascriptEnabled = true)
    ) {
      go to BeforeYouStartPage
      cacheSetup()
      go to CompleteAndConfirmPage
      Eventually.eventually(page.title == CompleteAndConfirmPage.title)

      println("PAGE TITLE: " + page.title )

      mileageTextBox enter CompleteAndConfirmPage.MileageValid
      dayDateOfSaleTextBox enter CompleteAndConfirmPage.DayDateOfSaleValid
      monthDateOfSaleTextBox enter CompleteAndConfirmPage.MonthDateOfSaleValid
      yearDateOfSaleTextBox enter CompleteAndConfirmPage.YearDateOfSaleValid
      click on consent

      val submitButton = next.underlying
      def clickSubmit(implicit driver: WebDriver) = driver.asInstanceOf[JavascriptExecutor]
        .executeScript("var clicks = 0; while (clicks < 5) {arguments[0].click(); clicks++;}", submitButton)

      submitButton.getAttribute("class") should not include "disabled"
      clickSubmit

      Thread.sleep(1000)
      countingWebService.calls should have size 1
    }*/

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

//    "redirect to vehicles lookup page if there is no allowGoingToCompleteAndConfirmPageCacheKey cookie e set" taggedAs UiTag in new WebBrowser {
//ToDo uncomment test when us1684 is developed
//      def deleteFlagCookie(implicit driver: WebDriver) =
//        driver.manage.deleteCookieNamed(AllowGoingToCompleteAndConfirmPageCacheKey)
//
//      go to BeforeYouStartPage
//      cacheSetup()
//      go to CompleteAndConfirmPage
//
//      mileageTextBox enter CompleteAndConfirmPage.MileageValid
//      dayDateOfSaleTextBox enter CompleteAndConfirmPage.DayDateOfSaleValid
//      monthDateOfSaleTextBox enter CompleteAndConfirmPage.MonthDateOfSaleValid
//      yearDateOfSaleTextBox enter CompleteAndConfirmPage.YearDateOfSaleValid
//      click on consent
//
//      deleteFlagCookie
//
//      click on next
//      page.title should equal(VehicleLookupPage.title)
//    }
//  }

  "back" should {
    "display NewKeeperChooseYourAddress when back link is clicked for a new keeper who has selected an address" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .privateKeeperDetails()
        .vehicleAndKeeperDetails()
        .newKeeperDetailsModel()
        .allowGoingToCompleteAndConfirmPageCookie()

      go to CompleteAndConfirmPage
      click on back
      page.title should equal(NewKeeperChooseYourAddressPage.title)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .newKeeperDetailsModel()
      .allowGoingToCompleteAndConfirmPageCookie()

//  class MockAppWebBrowser(webService: AcquireWebService) extends WebBrowser(
//    app = FakeApplication(withGlobal = Some(mockAcquireServiceCompositionGlobal(webService)))
//  )

//  val failingWebService = new FakeAcquireWebServiceImpl {
//    override def callAcquireService(request: AcquireRequestDto, trackingId: String): Future[WSResponse] =
//      throw new Exception("Mock web service failure")
//  }

//  val countingWebService = new FakeAcquireWebServiceImpl {
//    var calls = List[(AcquireRequestDto, String)]()
//
//    override def callAcquireService(request: AcquireRequestDto, trackingId: String): Future[WSResponse] = {
//      calls ++= List(request -> trackingId)
//      super.callAcquireService(request, trackingId)
//    }
//  }

//  def mockAcquireServiceCompositionGlobal(webService: AcquireWebService) = new GlobalLike with TestComposition {
//    override lazy val injector: Injector = TestGlobal.testInjector(new ScalaModule with MockitoSugar {
//      override def configure() {
//        bind[AcquireWebService].toInstance(webService)
//      }
//    })
//  }

  private val cookiesDeletedOnRedirect =
    VehicleNewKeeperCompletionCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private def assertCookiesDoesnExist(cookies: Set[String])(implicit driver: WebDriver) =
    for (cookie <- cookies) driver.manage().getCookieNamed(cookie) should be (null)

  private def assertCookieExist(implicit driver: WebDriver) =
    driver.manage().getCookieNamed(AllowGoingToCompleteAndConfirmPageCacheKey) should not be null

}
