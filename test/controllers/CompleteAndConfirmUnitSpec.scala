package controllers

import Common.PrototypeHtml
import composition.WithApplication
import helpers.CookieFactoryForUnitSpecs
import helpers.UnitSpec
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.CompleteAndConfirmFormModel.Form.ConsentId
import org.joda.time.Instant
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, times, verify, when}
import pages.changekeeper.BusinessKeeperDetailsPage.{BusinessNameValid, EmailValid, FleetNumberValid}
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue
import pages.changekeeper.DateOfSalePage.MileageValid
import pages.changekeeper.DateOfSalePage.YearDateOfSaleValid
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid}
import pages.changekeeper.VehicleLookupPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, contentAsString, defaultAwaitTimeout, LOCATION, OK}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import common.services.DateService
import common.services.SEND.EmailConfiguration
import common.testhelpers.CookieHelper
import common.testhelpers.CookieHelper.fetchCookiesFromHeaders
import common.views.models.DayMonthYear
import common.webserviceclients.acquire.{AcquireRequestDto, AcquireService}
import common.webserviceclients.emailservice.{EmailService, EmailServiceSendRequest, EmailServiceSendResponse, From}
import utils.helpers.Config
import webserviceclients.fakes.FakeAcquireWebServiceImpl.acquireResponseSuccess

class CompleteAndConfirmUnitSpec extends UnitSpec {
  "present" should {
    "display the page with new keeper cached" in new WithApplication {
      whenReady(present) { r =>
        r.header.status should equal(OK)
      }
    }

    "display prototype message when config set to true" in new WithApplication {
      contentAsString(present) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config] //TestConfig
      implicit val dateService = injector.getInstance(classOf[DateService])
      when(config.isPrototypeBannerVisible).thenReturn(false)

      val privateKeeperDetailsPrototypeNotVisible = new PrivateKeeperDetails()
      val result = privateKeeperDetailsPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "present a full form when cookie data is present for new keeper" in new WithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include( MileageValid)
      content should include( """value="true"""") // Checkbox value
      content should include( YearDateOfSaleValid)
    }

    "display empty fields when new keeper complete cookie does not exist" in new WithApplication {
      val request = FakeRequest()
      val result = completeAndConfirm.present(request)
      val content = contentAsString(result)
      content should not include MileageValid
    }

    "redirect to vehicle lookup when no new keeper details cookie is present" in new WithApplication {
      val request = FakeRequest()
      val result = completeAndConfirm.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to vehicle lookup when allowGoingToCompleteAndConfirmPage cookie is not set" in new WithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
      val result = completeAndConfirm.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to date of sale page when no dateOfSale cookies is present" in new WithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val result = completeAndConfirm.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "present the form when new keeper details cookie is present" in new WithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel())
      val result = completeAndConfirm.present(request)
      whenReady(result) { r =>
        r.header.status should equal(OK)
      }
    }

    "play back business keeper details as expected" in new WithApplication() {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel(
        businessName = Some(BusinessNameValid),
        fleetNumber = Some(FleetNumberValid),
        email = Some(EmailValid),
        isBusinessKeeper = true))
      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include("Fleet number")
      content should include(s"$BusinessNameValid")
      content should include(s"$FleetNumberValid")
      content should include(s"$EmailValid")
    }

    "play back private keeper details as expected" in new WithApplication() {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel(
        firstName = Some(FirstNameValid),
        lastName = Some(LastNameValid),
        email = Some(EmailValid),
        isBusinessKeeper = false
      )).withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include(s"$FirstNameValid")
      content should include(s"$LastNameValid")
      content should include(s"$EmailValid")
    }

    "play back date of sale as expected" in new WithApplication() {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel(
        firstName = Some(FirstNameValid),
        lastName = Some(LastNameValid),
        email = Some(EmailValid),
        isBusinessKeeper = false
      )).withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include(s"$FirstNameValid")
      content should include(s"$LastNameValid")
      content should include(s"$EmailValid")
    }
  }

  "submit" should {
    "return a bad request if consent is not ticked" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(consent = "")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val result = completeAndConfirm.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        fetchCookiesFromHeaders(r).map(_.name) should not contain AllowGoingToCompleteAndConfirmPageCacheKey
      }
    }

    "call the micro services and redirect to the next page if consent has been ticked" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createControllerAndMocks

      val result = completeAndConfirm.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
        CookieHelper.verifyCookieHasBeenDiscarded(AllowGoingToCompleteAndConfirmPageCacheKey, fetchCookiesFromHeaders(r))
        verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], any[TrackingId])
        verify(emailServiceMock, times(1)).invoke(any[EmailServiceSendRequest], any[TrackingId])
      }
    }

    "return a bad request and not call the micro services if consent is not ticked" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(consent = "")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
        .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createControllerAndMocks

      val result = completeAndConfirm.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(acquireServiceMock, never()).invoke(any[AcquireRequestDto], any[TrackingId])
        verify(emailServiceMock, never()).invoke(any[EmailServiceSendRequest], any[TrackingId])
      }
    }
  }

  private def completeAndConfirmController(acquireService: AcquireService, emailService: EmailService)
                               (implicit config: Config = mockConfig,
                                dateService: DateService = dateServiceStubbed()): CompleteAndConfirm = {
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    new CompleteAndConfirm(acquireService, emailService)
  }

  private def dateServiceStubbed(day: Int = 1,
                                 month: Int = 1,
                                 year: Int = 2014) = {
    val dateService = mock[DateService]
    when(dateService.today).thenReturn(new DayMonthYear(day = day,
      month = month,
      year = year))

    val instant = new DayMonthYear(day = day,
      month = month,
      year = year).toDateTime.get.getMillis

    when(dateService.now).thenReturn(new Instant(instant))
    dateService
  }

  private val mockConfig: Config = {
    val config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(true)
    when(config.googleAnalyticsTrackingId).thenReturn(Some("trackingId"))
    when(config.assetsUrl).thenReturn(None)

    val emailConfiguration = EmailConfiguration(
      from = From(email = "", name = ""), feedbackEmail = From(email = "", name = ""),
      whiteList = None)
    when(config.emailConfiguration). thenReturn(emailConfiguration)
    config
  }

  def createControllerAndMocks: (AcquireService, EmailService, CompleteAndConfirm) = {
    val acquireServiceMock: AcquireService = mock[AcquireService]
    when(acquireServiceMock.invoke(any[AcquireRequestDto], any[TrackingId])).
      thenReturn(Future.successful {
      (OK, Some(acquireResponseSuccess))
    })

    val emailServiceMock: EmailService = mock[EmailService]
    when(emailServiceMock.invoke(any[EmailServiceSendRequest](), any[TrackingId])).
      thenReturn(Future(EmailServiceSendResponse()))

    val completeAndConfirm = completeAndConfirmController(acquireServiceMock, emailServiceMock)

    (acquireServiceMock, emailServiceMock, completeAndConfirm)
  }

  private def buildCorrectlyPopulatedRequest(consent: String = ConsentTrue) = {
    FakeRequest().withFormUrlEncodedBody(
      ConsentId -> consent
    ).withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
     .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
     .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
  }

  private def completeAndConfirm = {
    injector.getInstance(classOf[CompleteAndConfirm])
  }

  private lazy val present = {
    val request = FakeRequest()
      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
    completeAndConfirm.present(request)
  }
}
