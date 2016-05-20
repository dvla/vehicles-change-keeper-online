package controllers

import com.tzavellas.sse.guice.ScalaModule
import controllers.Common.PrototypeHtml
import helpers.{TestWithApplication, UnitSpec}
import helpers.changekeeper.CookieFactoryForUnitSpecs
import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.{DateOfSaleFormModel, IdentifierCacheKey}
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.joda.time.format.DateTimeFormat
import org.mockito.Mockito.when
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BusinessKeeperDetailsPage.{BusinessNameValid, FleetNumberValid}
import pages.changekeeper.DateOfSalePage.{DayDateOfSaleValid, MonthDateOfSaleValid, YearDateOfSaleValid}
import pages.changekeeper.PrivateKeeperDetailsPage.{EmailValid, FirstNameValid, LastNameValid}
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper.{fetchCookiesFromHeaders, verifyCookieHasBeenDiscarded}
import utils.helpers.Config
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{RegistrationNumberValid, TransactionIdValid, TransactionTimestampValid, VehicleMakeValid, VehicleModelValid}

class ChangeKeeperSuccessUnitSpec extends UnitSpec {

  val testUrl = "http://test/survery/url"

  "present" should {
    "display the page with new keeper cached" in new TestWithApplication {
      whenReady(present) { r =>
        r.header.status should equal(OK)
      }
    }

    "display prototype message when config set to true" in new TestWithApplication {
      contentAsString(present) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new TestWithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      implicit val surveyUrl = injector.getInstance(classOf[SurveyUrl])
      when(config.isPrototypeBannerVisible).thenReturn(false)

      val acquireSuccessPrototypeNotVisible = new ChangeKeeperSuccess()
      val result = acquireSuccessPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "redirect to before you start when no completion cookie is present" in new TestWithApplication {
      val request = FakeRequest()
      val result = changeKeeperSuccess.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "present a full page with private keeper cached details " +
      "when all cookies are present for new keeper success" in new TestWithApplication {
      val fmt = DateTimeFormat.forPattern("dd/MM/yyyy")

      val request = fakeRequest.withCookies(
        CookieFactoryForUnitSpecs.newKeeperDetailsModel(
          firstName = Some(FirstNameValid),
          lastName = Some(LastNameValid),
          email = Some(EmailValid)
        )
      )

      val content = contentAsString(changeKeeperSuccess.present(request))
      content should include(RegistrationNumberValid)
      content should include(VehicleMakeValid)
      content should include(VehicleModelValid)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include(EmailValid)
      content should include(YearDateOfSaleValid)
      content should include(MonthDateOfSaleValid)
      content should include(DayDateOfSaleValid)
      content should include(fmt.print(TransactionTimestampValid))
      content should include(TransactionIdValid)
    }

    "present a full page with business keeper cached details " +
      "when all cookies are present for new keeper success" in new TestWithApplication {
      val fmt = DateTimeFormat.forPattern("dd/MM/yyyy")

      val request = fakeRequest.withCookies(
        CookieFactoryForUnitSpecs.newKeeperDetailsModel(
          businessName = Some(BusinessNameValid),
          fleetNumber = Some(FleetNumberValid),
          email = Some(EmailValid)
        )
      )

      val content = contentAsString(changeKeeperSuccess.present(request))
      content should include(RegistrationNumberValid)
      content should include(VehicleMakeValid)
      content should include(VehicleModelValid)
      content should include(BusinessNameValid)
      content should include(FleetNumberValid)
      content should include(EmailValid)
      content should include(YearDateOfSaleValid)
      content should include(MonthDateOfSaleValid)
      content should include(DayDateOfSaleValid)
      content should include(fmt.print(TransactionTimestampValid))
      content should include(TransactionIdValid)
    }

    "contain code in the page source to open the survey in a new tab " +
      "when a survey url is configured" in new TestWithApplication {
      val request = fakeRequest.withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccessWithMockConfig(mockSurveyConfig()).present(request)
      val expectedContent = s"window.open('$testUrl', '_blank')"
      contentAsString(result) should include(expectedContent)
    }

    "not contain code in the page source to open the survey in a new tab " +
      "when a survey url is configured" in new TestWithApplication {
      val request = fakeRequest.withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccessWithMockConfig(mockSurveyConfig(surveyUrl = None)).present(request)
      val expectedContent = s"window.open('$testUrl', '_blank')"
      contentAsString(result) should not include expectedContent
    }
  }

  "finish" should {
    "discard the vehicle, new keeper and confirm cookies" in {
      val request = fakeRequest
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        verifyDiscardedCookies(fetchCookiesFromHeaders(r))
      }
    }

    "discard the vehicle, new keeper and confirm cookies with ceg identifier" in {
      val identifier = "CEG"

      val request = fakeRequest
        .withCookies(CookieFactoryForUnitSpecs.withIdentifier(identifier))
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        verifyDiscardedCookies(cookies)
        verifyCookieHasBeenDiscarded(IdentifierCacheKey, cookies)
      }
    }

    "redirect to gov.uk" in {
      val request = fakeRequest
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some("https://www.gov.uk"))
      }
    }
  }

  private lazy val changeKeeperSuccess = {
    injector.getInstance(classOf[ChangeKeeperSuccess])
  }

  private lazy val fakeRequest =
    FakeRequest()
      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
      .withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel())
      .withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel())

  private def verifyDiscardedCookies(cookies: Seq[play.api.mvc.Cookie]) = {
    verifyCookieHasBeenDiscarded(vehicleAndKeeperLookupDetailsCacheKey, cookies)
    verifyCookieHasBeenDiscarded(VehicleLookupFormModelCacheKey, cookies)
    verifyCookieHasBeenDiscarded(newKeeperDetailsCacheKey, cookies)
    verifyCookieHasBeenDiscarded(privateKeeperDetailsCacheKey, cookies)
    verifyCookieHasBeenDiscarded(businessKeeperDetailsCacheKey, cookies)
    verifyCookieHasBeenDiscarded(DateOfSaleFormModel.DateOfSaleCacheKey, cookies)
    verifyCookieHasBeenDiscarded(CompleteAndConfirmCacheKey, cookies)
    verifyCookieHasBeenDiscarded(ChangeKeeperCompletionResponseCacheKey, cookies)
  }

  private lazy val present = {
    val request = fakeRequest
      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
    changeKeeperSuccess.present(request)
  }

  def changeKeeperSuccessWithMockConfig(config: Config): ChangeKeeperSuccess =
    testInjector(new ScalaModule() {
      override def configure(): Unit = bind[Config].toInstance(config)
    }).getInstance(classOf[ChangeKeeperSuccess])

  def mockSurveyConfig(surveyUrl: Option[String] = Some(testUrl)): Config = {
    val config = mock[Config]
    when(config.assetsUrl).thenReturn(None)
    when(config.googleAnalyticsTrackingId).thenReturn(None)
    when(config.surveyUrl).thenReturn(surveyUrl)
    config
  }
}
