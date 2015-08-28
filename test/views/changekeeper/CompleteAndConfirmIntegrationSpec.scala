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

  "submit button" should {
    //    "go to the appropriate next page when all details are entered for a new keeper" taggedAs UiTag in new WebBrowser {
    // ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      navigate()
    //      page.title should equal(AcquireSuccessPage.title)
    //    }

    //    "go to the AcquireFailure page when all details are entered " +
    //      "for a new keeper" taggedAs UiTag in new MockAppWebBrowser(failingWebService) {
    // ToDo uncomment test when us1684 is developed
    //      go to BeforeYouStartPage
    //      cacheSetup()
    //      navigate()
    //      page.title should equal(Messages("error.title"))
    //    }

    //    "go to the appropriate next page when mandatory details are entered " +
    //      "for a new keeper" taggedAs UiTag in new WebBrowser {
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

    //    "clear off the preventGoingToCompleteAndConfirmPage cookie " +
    //      "on failure" taggedAs UiTag in new MockAppWebBrowser(failingWebService) {
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

  }

//    "redirect to vehicles lookup page " +
//      "if there is no allowGoingToCompleteAndConfirmPageCacheKey cookie e set" taggedAs UiTag in new WebBrowser {
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

  /*
  private def assertCookieExist(implicit driver: WebDriver) =
    driver.manage().getCookieNamed(AllowGoingToCompleteAndConfirmPageCacheKey) should not be null
   */
}
