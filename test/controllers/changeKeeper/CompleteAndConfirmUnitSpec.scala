package controllers.changeKeeper

import controllers.changeKeeper.Common.PrototypeHtml
import controllers.{CompleteAndConfirm, PrivateKeeperDetails}
import helpers.UnitSpec
import helpers.CookieFactoryForUnitSpecs
import models.CompleteAndConfirmFormModel.Form.{MileageId, DateOfSaleId, ConsentId}
import org.joda.time.{DateTime, Instant}
import org.joda.time.format.DateTimeFormat
import org.mockito.Matchers.{anyString, any}
import org.mockito.Mockito.{never, times, verify, when}
import pages.changekeeper.BusinessKeeperDetailsPage.{BusinessNameValid, EmailValid, FleetNumberValid}
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue
import pages.changekeeper.CompleteAndConfirmPage.DayDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.MileageValid
import pages.changekeeper.CompleteAndConfirmPage.MonthDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.YearDateOfSaleValid
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid}
import pages.changekeeper.VehicleLookupPage
import play.api.test.Helpers.{LOCATION, BAD_REQUEST, OK, contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest, WithApplication}
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.mappings.DayMonthYear.{DayId, MonthId, YearId}
import common.services.DateService
import common.services.SEND.{From, EmailConfiguration}
import common.views.models.DayMonthYear
import common.webserviceclients.acquire.{AcquireRequestDto, AcquireService}
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
        .withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include( s"""value="$MileageValid"""")
      content should include( """value="true"""") // Checkbox value
      content should include( s"""value="$YearDateOfSaleValid"""")
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

    "present the form when new keeper details cookie is present" in new WithApplication {
    val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
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
      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include("<dt>Fleet number</dt>")
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
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
      val content = contentAsString(completeAndConfirm.present(request))
      content should include(s"$FirstNameValid")
      content should include(s"$LastNameValid")
      content should include(s"$EmailValid")
    }
  }

  "submit" should {
    "replace numeric mileage error message for with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(mileage = "$$")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())

      val result = completeAndConfirm.submitWithDateCheck(request)
      val replacementMileageErrorMessage = "You must enter a valid mileage between 0 and 999999"
      replacementMileageErrorMessage.r.findAllIn(contentAsString(result)).length should equal(2)
    }

    "return a bad request if consent is not ticked" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(consent = "")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val result = completeAndConfirm.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "redirect to next page when mandatory fields are complete for new keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val acquireServiceMock = mock[AcquireService]
      val completeAndConfirm = completeAndConfirmController(acquireServiceMock)

      when(acquireServiceMock.invoke(any[AcquireRequestDto], any[String])).
        thenReturn(Future.successful {
        (OK, Some(acquireResponseSuccess))
      })

      val result = completeAndConfirm.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
        verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
      }
    }

    "not call the micro service when the date of acquisition is before the date of disposal and return a bad request" in new WithApplication {
      // The date of acquisition is 19-10-2012
      val disposalDate = DateTime.parse("20-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val acquireServiceMock = mock[AcquireService]
      val completeAndConfirm = completeAndConfirmController(acquireServiceMock)

      val result = completeAndConfirm.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(acquireServiceMock, never()).invoke(any[AcquireRequestDto], anyString())
      }
    }

    "call the micro service when the date of acquisition is the same as the date of disposal and redirect to the next page" in new WithApplication {
      // The date of acquisition is 19-10-2012
      val disposalDate = DateTime.parse("19-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
        .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())

      val acquireServiceMock = mock[AcquireService]
      val completeAndConfirm = completeAndConfirmController(acquireServiceMock)

      when(acquireServiceMock.invoke(any[AcquireRequestDto], any[String])).
        thenReturn(Future.successful {
        (OK, Some(acquireResponseSuccess))
      })

      val result = completeAndConfirm.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
        verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
      }
    }

  }

  private def completeAndConfirmController(acquireService: AcquireService)
                               (implicit config: Config = mockConfig,
                                dateService: DateService = dateServiceStubbed()): CompleteAndConfirm = {
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    new CompleteAndConfirm(acquireService)
  }

  /*
  private def acquireWebService(acquireServiceStatus: Int = OK,
                                acquireServiceResponse: Option[AcquireResponseDto] = Some(acquireResponseSuccess)): AcquireService = {
    val acquireWebService = mock[AcquireWebService]
    when(acquireWebService.callAcquireService(any[AcquireRequestDto], any[String])).
      thenReturn(Future.successful {
      val fakeJson = acquireServiceResponse map (Json.toJson(_))
      new FakeResponse(status = acquireServiceStatus, fakeJson = fakeJson) // Any call to a webservice will always return this successful response.
    })

    acquireWebService
  }
*/
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
//    when(config.acquire).thenReturn(new AcquireConfig)
    when(config.assetsUrl).thenReturn(None)

    val emailConfiguration = EmailConfiguration(host = "localhost", port = 80,
      username = "username", password = "password",
      from = From(email = "", name = ""), feedbackEmail = From(email = "", name = ""),
      whiteList = None)
    when(config.emailConfiguration). thenReturn(emailConfiguration)
    config
  }


//  private def acquireController(acquireWebService: AcquireWebService): CompleteAndConfirm = {
//    val acquireService = new AcquireServiceImpl(config.acquire, acquireWebService)
//    acquireController(acquireWebService, acquireService)
//  }

//  private def acquireController(acquireWebService: AcquireWebService, acquireService: AcquireService)
//                               (implicit config: Config = config, dateService: DateService = dateServiceStubbed()):
//  CompleteAndConfirm = {
//    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
//
//    new CompleteAndConfirm(acquireService)
//  }

  private def buildCorrectlyPopulatedRequest(mileage: String = MileageValid,
                                             dayDateOfSale: String = DayDateOfSaleValid,
                                             monthDateOfSale: String = MonthDateOfSaleValid,
                                             yearDateOfSale: String = YearDateOfSaleValid,
                                             consent: String = ConsentTrue) = {
    FakeRequest().withFormUrlEncodedBody(
      MileageId -> mileage,
      s"$DateOfSaleId.$DayId" -> dayDateOfSale,
      s"$DateOfSaleId.$MonthId" -> monthDateOfSale,
      s"$DateOfSaleId.$YearId" -> yearDateOfSale,
      ConsentId -> consent
    )
      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
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
      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
    completeAndConfirm.present(request)
  }
}
