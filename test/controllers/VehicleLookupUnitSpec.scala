package controllers

import composition.{TestConfig, WithApplication}
import helpers.{UnitSpec, CookieFactoryForUnitSpecs}
import models.VehicleLookupFormModel.Form.DocumentReferenceNumberId
import models.VehicleLookupFormModel.Form.VehicleRegistrationNumberId
import models.VehicleLookupFormModel.Form.VehicleSoldToId
import models.VehicleLookupFormModel.Form.VehicleSellerEmailOption
import org.mockito.invocation.InvocationOnMock
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{never, times, when, verify}
import org.mockito.stubbing.Answer
import pages.changekeeper.MicroServiceErrorPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.VrmLockedPage
import pages.changekeeper.VehicleLookupFailurePage
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, contentAsString, defaultAwaitTimeout, LOCATION}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.mappings.{DocumentReferenceNumber, OptionalToggle}
import common.services.DateServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.healthstats.HealthStats
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupResponse
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupServiceImpl
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.vehicleDetailsResponseUnhandledException
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.vehicleDetailsResponseDocRefNumberNotLatest
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.vehicleDetailsResponseSuccess
import utils.helpers.Config
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.VrmAttempt2
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.VrmLocked
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseFirstAttempt
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.responseSecondAttempt
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.VrmThrows
import webserviceclients.fakes.{FakeDateServiceImpl, FakeResponse}

class VehicleLookupUnitSpec extends UnitSpec {

  val healthStatsMock = mock[HealthStats]
  when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
    override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
  })

  "present" should {
    "display the page" in new WithApplication {
      present.futureValue.header.status should equal(play.api.http.Status.OK)
    }

    "display populated fields when cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
      val result = vehicleLookupResponseGenerator().present(request)
      val content = contentAsString(result)
      content should include(ReferenceNumberValid)
      content should include(RegistrationNumberValid)
    }

    "display empty fields when cookie does not exist" in new WithApplication {
      val request = FakeRequest()
      val result = vehicleLookupResponseGenerator().present(request)
      val content = contentAsString(result)
      content should not include ReferenceNumberValid
      content should not include RegistrationNumberValid
    }
  }

  "submit" should {
    "replace max length error message for document reference number with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(referenceNumber = "1" * (DocumentReferenceNumber.MaxLength + 1))
      val result = vehicleLookupResponseGenerator().submit(request)
      // check the validation summary text
      "Document reference number - Document reference number must be an 11-digit number".
        r.findAllIn(contentAsString(result)).length should equal(1)
      // check the form item validation
      "\"error\">Document reference number must be an 11-digit number".
        r.findAllIn(contentAsString(result)).length should equal(1)
    }

    "replace required and min length error messages for document reference number with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(referenceNumber = "")
      val result = vehicleLookupResponseGenerator().submit(request)
      // check the validation summary text
      "Document reference number - Document reference number must be an 11-digit number".
        r.findAllIn(contentAsString(result)).length should equal(1)
      // check the form item validation
      "\"error\">Document reference number must be an 11-digit number".
        r.findAllIn(contentAsString(result)).length should equal(1)
    }

    "replace max length error message for vehicle registration number with standard error message (US43)" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = "PJ05YYYX")
      val result = vehicleLookupResponseGenerator().submit(request)
      val count = "Vehicle registration number must be valid format".r.findAllIn(contentAsString(result)).length

      count should equal(2)
    }

    "replace required and min length error messages for vehicle registration number with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = "")
        val result = vehicleLookupResponseGenerator().submit(request)
      val count = "Vehicle registration number must be valid format".r.findAllIn(contentAsString(result)).length

      count should equal(2) // The same message is displayed in 2 places - once in the validation-summary at the top of the page and once above the field.
    }

    "replace required error message for new keeper type (private keeper or business)" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(soldTo = "")
      val result = vehicleLookupResponseGenerator().submit(request)
      val count = "Select whether the vehicle is being sold to a private individual or business".r.findAllIn(contentAsString(result)).length

      count should equal(2)
    }

    "redirect to MicroserviceError when microservice throws" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = vehicleLookupError.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(MicroServiceErrorPage.address))
      }
    }

    "redirect to VehicleLookupFailure after a submit and unhandled exception returned by the fake microservice" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = vehicleLookupResponseGenerator(vehicleDetailsResponseUnhandledException).submit(request)

      result.futureValue.header.headers.get(LOCATION) should equal(Some(VehicleLookupFailurePage.address))
    }

    "redirect to vrm locked when valid submit and brute force prevention returns not permitted" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = VrmLocked)
      val result = vehicleLookupResponseGenerator(
        vehicleDetailsResponseDocRefNumberNotLatest,
        bruteForceService = bruteForceServiceImpl(permitted = false)
      ).submit(request)
      result.futureValue.header.headers.get(LOCATION) should equal(Some(VrmLockedPage.address))
    }

    "redirect to VehicleLookupFailure and display 1st attempt message when document reference number not found and security service returns 1st attempt" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = RegistrationNumberValid)
      val result = vehicleLookupResponseGenerator(
        vehicleDetailsResponseDocRefNumberNotLatest,
        bruteForceService = bruteForceServiceImpl(permitted = true)
      ).submit(request)

      result.futureValue.header.headers.get(LOCATION) should equal(Some(VehicleLookupFailurePage.address))
    }

    "redirect to VehicleLookupFailure and display 2nd attempt message when document reference number not found and security service returns 2nd attempt" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = VrmAttempt2)
      val result = vehicleLookupResponseGenerator(
        vehicleDetailsResponseDocRefNumberNotLatest,
        bruteForceService = bruteForceServiceImpl(permitted = true)
      ).submit(request)

      result.futureValue.header.headers.get(LOCATION) should equal(Some(VehicleLookupFailurePage.address))
    }

    "redirect to VrmLocked when document reference number when vehicles lookup not permitted" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(registrationNumber = VrmLocked)
      val result = vehicleLookupResponseGenerator(
        vehicleDetailsResponseDocRefNumberNotLatest,
        bruteForceService = bruteForceServiceImpl(permitted = false)
      ).submit(request)

      result.futureValue.header.headers.get(LOCATION) should equal(Some(VrmLockedPage.address))
    }

    "call the vehicle lookup micro service and brute force service after a valid request" in new WithApplication {
      val (bruteForceService, bruteForceWebServiceMock) = bruteForceServiceAndWebServiceMock(permitted = true)
      val (vehicleLookupController, vehicleLookupMicroServiceMock) = vehicleLookupControllerAndMocks(bruteForceService = bruteForceService)
      val request = buildCorrectlyPopulatedRequest()
      val result = vehicleLookupController.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(PrivateKeeperDetailsPage.address))
        verify(bruteForceWebServiceMock, times(1)).callBruteForce(anyString())
        verify(vehicleLookupMicroServiceMock, times(1)).invoke(any[VehicleAndKeeperDetailsRequest], anyString())
      }
    }

    "not call the vehicle lookup micro service after a invalid request" in new WithApplication {
      val (bruteForceService, bruteForceWebServiceMock) = bruteForceServiceAndWebServiceMock(permitted = true)
      val (vehicleLookupController, vehicleLookupMicroServiceMock) = vehicleLookupControllerAndMocks(bruteForceService = bruteForceService)
      val request = buildCorrectlyPopulatedRequest(registrationNumber = "")
      val result = vehicleLookupController.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(bruteForceWebServiceMock, never()).callBruteForce(anyString())
        verify(vehicleLookupMicroServiceMock, never()).invoke(any[VehicleAndKeeperDetailsRequest], anyString())
      }
    }
  }

  private def responseThrows: Future[WSResponse] = Future {
    throw new RuntimeException("This error is generated deliberately by a test")
  }

  private def bruteForceServiceImpl(permitted: Boolean): BruteForcePreventionService = {
    val (bruteForcePreventionService, _) = bruteForceServiceAndWebServiceMock(permitted)
    bruteForcePreventionService
  }

  private def bruteForceServiceAndWebServiceMock(permitted: Boolean): (BruteForcePreventionService, BruteForcePreventionWebService) = {
    def bruteForcePreventionWebService: BruteForcePreventionWebService = {
      val status = if (permitted) play.api.http.Status.OK else play.api.http.Status.FORBIDDEN
      val bruteForcePreventionWebServiceMock = mock[BruteForcePreventionWebService]

      when(bruteForcePreventionWebServiceMock.callBruteForce(RegistrationNumberValid)).
        thenReturn(Future.successful(new FakeResponse(status = status, fakeJson = responseFirstAttempt)))

      when(bruteForcePreventionWebServiceMock.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmAttempt2)).
        thenReturn(Future.successful(new FakeResponse(status = status, fakeJson = responseSecondAttempt)))

      when(bruteForcePreventionWebServiceMock.callBruteForce(FakeBruteForcePreventionWebServiceImpl.VrmLocked)).
        thenReturn(Future.successful(new FakeResponse(status = status)))

      when(bruteForcePreventionWebServiceMock.callBruteForce(VrmThrows)).thenReturn(responseThrows)

      when(bruteForcePreventionWebServiceMock.reset(any[String])).
        thenReturn(Future.successful(new FakeResponse(status = play.api.http.Status.OK)))

      bruteForcePreventionWebServiceMock
    }

    val bruteForcePreventionWebServiceMock = bruteForcePreventionWebService
    val bruteForcePreventionService = new BruteForcePreventionServiceImpl(
      config = new TestBruteForcePreventionConfig,
      ws = bruteForcePreventionWebServiceMock,
      healthStatsMock,
      dateService = new FakeDateServiceImpl
    )
    (bruteForcePreventionService, bruteForcePreventionWebServiceMock)
  }

  private def vehicleLookupControllerAndMocks(fullResponse: (Int, Option[VehicleAndKeeperLookupResponse]) = vehicleDetailsResponseSuccess,
                                              bruteForceService: BruteForcePreventionService = bruteForceServiceImpl(permitted = true)
                                               ): (VehicleLookup, VehicleAndKeeperLookupWebService) = {
    val (status, vehicleDetailsResponse) = fullResponse
    val wsMock = mock[VehicleAndKeeperLookupWebService]
    when(wsMock.invoke(any[VehicleAndKeeperDetailsRequest], any[String])).thenReturn(Future {
      val responseAsJson: Option[JsValue] = vehicleDetailsResponse match {
        case Some(e) => Some(Json.toJson(e))
        case _ => None
      }
      new FakeResponse(status = status, fakeJson = responseAsJson) // Any call to a webservice will always return this successful response.
    })
    val vehicleAndKeeperLookupServiceImpl = new VehicleAndKeeperLookupServiceImpl(wsMock, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
    when(config.assetsUrl).thenReturn(None) // Stub this config value.

    val vehicleLookupController = new VehicleLookup()(
      bruteForceService = bruteForceService,
      vehicleLookupService = vehicleAndKeeperLookupServiceImpl,
      dateService = dateService,
      clientSideSessionFactory,
      config
    )
    (vehicleLookupController, wsMock)
  }

  private def vehicleLookupResponseGenerator(fullResponse: (Int, Option[VehicleAndKeeperLookupResponse]) = vehicleDetailsResponseSuccess,
                                             bruteForceService: BruteForcePreventionService = bruteForceServiceImpl(permitted = true)) = {
    val (status, vehicleDetailsResponse) = fullResponse
    val ws: VehicleAndKeeperLookupWebService = mock[VehicleAndKeeperLookupWebService]
    when(ws.invoke(any[VehicleAndKeeperDetailsRequest], any[String])).thenReturn(Future {
      val responseAsJson: Option[JsValue] = vehicleDetailsResponse match {
        case Some(e) => Some(Json.toJson(e))
        case _ => None
      }
      new FakeResponse(status = status, fakeJson = responseAsJson) // Any call to a webservice will always return this successful response.
    })
    val vehicleAndKeeperLookupServiceImpl = new VehicleAndKeeperLookupServiceImpl(ws, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = new TestConfig

    new VehicleLookup()(
      bruteForceService = bruteForceService,
      vehicleLookupService = vehicleAndKeeperLookupServiceImpl,
      dateService = dateService,
      clientSideSessionFactory,
      config
    )
  }

  private lazy val vehicleLookupError = {
    val permitted = true // The lookup is permitted as we want to test failure on the vehicle lookup micro-service step.
    val vehicleAndKeeperLookupWebService: VehicleAndKeeperLookupWebService = mock[VehicleAndKeeperLookupWebService]
    when(vehicleAndKeeperLookupWebService.invoke(any[VehicleAndKeeperDetailsRequest], any[String])).thenReturn(Future {
      throw new IllegalArgumentException
    })
    val vehicleAndKeeperLookupServiceImpl = new VehicleAndKeeperLookupServiceImpl(vehicleAndKeeperLookupWebService, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]

    new VehicleLookup()(
      bruteForceService = bruteForceServiceImpl(permitted = permitted),
      vehicleLookupService = vehicleAndKeeperLookupServiceImpl,
      dateService = dateService,
      clientSideSessionFactory,
      config
    )
  }

  private def buildCorrectlyPopulatedRequest(referenceNumber: String = ReferenceNumberValid,
                                             registrationNumber: String = RegistrationNumberValid,
                                             soldTo: String = VehicleSoldTo_Private) = {
    FakeRequest().withFormUrlEncodedBody(
      DocumentReferenceNumberId -> referenceNumber,
      VehicleRegistrationNumberId -> registrationNumber,
      VehicleSellerEmailOption -> OptionalToggle.Invisible,
      VehicleSoldToId -> soldTo
    )
  }

  private lazy val present = {
    val request = FakeRequest()
    vehicleLookupResponseGenerator(vehicleDetailsResponseSuccess).present(request)
  }

  private val dateService = new DateServiceImpl
}
