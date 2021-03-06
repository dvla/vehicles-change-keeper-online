package controllers

import helpers.TestWithApplication
import helpers.changekeeper.{ValidVRMFormat, InvalidVRMFormat}
import InvalidVRMFormat.allInvalidVrmFormats
import helpers.UnitSpec
import ValidVRMFormat.allValidVrmFormats
import models.VehicleLookupFormModel.Form.DocumentReferenceNumberId
import models.VehicleLookupFormModel.Form.VehicleRegistrationNumberId
import models.VehicleLookupFormModel.Form.VehicleSellerEmail
import models.VehicleLookupFormModel.Form.VehicleSellerEmailOption
import models.VehicleLookupFormModel.Form.VehicleSoldToId
import org.mockito.invocation.InvocationOnMock
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.when
import org.mockito.stubbing.Answer
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import common.mappings.Email.{EmailId, EmailVerifyId}
import common.mappings.OptionalToggle
import common.services.DateServiceImpl
import common.testhelpers.RandomVrmGenerator
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.healthstats.HealthStats
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupFailureResponse
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupRequest
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupServiceImpl
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupSuccessResponse
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService
import utils.helpers.Config
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.FakeDateServiceImpl
import webserviceclients.fakes.FakeResponse
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ConsentValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.vehicleDetailsResponseSuccess

class VehicleLookupFormSpec extends UnitSpec {

  val healthStatsMock = mock[HealthStats]
  when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
    override def answer(invocation: InvocationOnMock): Future[_] = invocation.getArguments()(1).asInstanceOf[Future[_]]
  })
  "form" should {
    "accept when all fields contain valid responses" in new TestWithApplication {
      formWithValidDefaults().get.referenceNumber should equal(ReferenceNumberValid)
      formWithValidDefaults().get.registrationNumber should equal(RegistrationNumberValid)
    }

    "accept when all fields contain valid responses and user entered an email" in new TestWithApplication {
      val email = Some("anemail@test.com")
      formWithValidDefaults().get.referenceNumber should equal(ReferenceNumberValid)
      formWithValidDefaults().get.registrationNumber should equal(RegistrationNumberValid)
      formWithValidDefaults(sellerEmail = email).get.sellerEmail should equal (email)
    }
  }

  "referenceNumber" should {
    allInvalidVrmFormats.foreach(vrm => "reject invalid vehicle registration mark : " + vrm in new TestWithApplication {
      formWithValidDefaults(registrationNumber = vrm).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    })

    allValidVrmFormats.foreach(vrm => "accept valid vehicle registration mark : " + vrm in new TestWithApplication {
      formWithValidDefaults(registrationNumber = vrm).get.registrationNumber should equal(vrm)
    })

    "reject if blank" in new TestWithApplication {
      val vehicleLookupFormError = formWithValidDefaults(referenceNumber = "").errors
      val expectedKey = DocumentReferenceNumberId

      vehicleLookupFormError should have length 3
      vehicleLookupFormError.head.key should equal(expectedKey)
      vehicleLookupFormError.head.message should equal("error.minLength")
      vehicleLookupFormError(1).key should equal(expectedKey)
      vehicleLookupFormError(1).message should equal("error.required")
      vehicleLookupFormError(2).key should equal(expectedKey)
      vehicleLookupFormError(2).message should equal("error.restricted.validNumberOnly")
    }

    "reject if less than min length" in new TestWithApplication {
      formWithValidDefaults(referenceNumber = "1234567891").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if wrong email is present" in new TestWithApplication {
      formWithValidDefaults(sellerEmail = Some("invalid email")).errors
        .flatMap(_.messages) should contain theSameElementsAs
        List("error.email")
    }

    "reject if greater than max length" in new TestWithApplication {
      formWithValidDefaults(referenceNumber = "123456789101").errors
        .flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if contains letters" in new TestWithApplication {
      formWithValidDefaults(referenceNumber = "qwertyuiopl").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validNumberOnly")
    }

    "reject if contains special characters" in new TestWithApplication {
      formWithValidDefaults(referenceNumber = "£££££££££££").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validNumberOnly")
    }

    "accept if valid" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = RegistrationNumberValid).get
        .referenceNumber should equal(ReferenceNumberValid)
    }
  }

  "registrationNumber" should {
    "reject if empty" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.restricted.validVrnOnly")
    }

    "reject if less than min length" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validVrnOnly")
    }

    "reject if more than max length" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = "AB53WERT").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "reject if more than max length 2" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = "PJ056YYY").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "reject if contains special characters" in new TestWithApplication {
      formWithValidDefaults(registrationNumber = "ab53ab%").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "accept a selection of randomly generated vrms that all satisfy vrm regex" in new TestWithApplication {
      for (i <- 1 to 20) {
        val randomVrm = RandomVrmGenerator.vrm
        formWithValidDefaults(registrationNumber = randomVrm).get.registrationNumber should equal(randomVrm)
      }
    }
  }

  private val bruteForceServiceImpl: BruteForcePreventionService = {
    val bruteForcePreventionWebService: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
    when(bruteForcePreventionWebService.callBruteForce(anyString(), any[TrackingId])).
      thenReturn( Future.successful( new FakeResponse(status = OK) ))

    new BruteForcePreventionServiceImpl(
      config = new TestBruteForcePreventionConfig,
      ws = bruteForcePreventionWebService,
      healthStatsMock,
      dateService = new FakeDateServiceImpl
    )
  }

  val dateService = new DateServiceImpl

  private def vehicleLookupResponseGenerator(
    fullResponse:(Int, Option[Either[VehicleAndKeeperLookupFailureResponse, VehicleAndKeeperLookupSuccessResponse]])) = {
    val vehicleAndKeeperLookupWebService: VehicleAndKeeperLookupWebService = mock[VehicleAndKeeperLookupWebService]
    when(vehicleAndKeeperLookupWebService.invoke(any[VehicleAndKeeperLookupRequest], any[TrackingId]))
      .thenReturn(Future {
      val responseAsJson : Option[JsValue] = fullResponse._2 match {
        case Some(response) => response match {
          case Left(failure) => Some(Json.toJson(failure))
          case Right(success) => Some(Json.toJson(success))
        }
        case _ => None
      }
      // Any call to a webservice will always return this successful response.
      new FakeResponse(status = fullResponse._1, fakeJson = responseAsJson)
    })
    val vehicleAndKeeperLookupServiceImpl =
      new VehicleAndKeeperLookupServiceImpl(vehicleAndKeeperLookupWebService, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    new VehicleLookup()(
      bruteForceService = bruteForceServiceImpl,
      vehicleLookupService = vehicleAndKeeperLookupServiceImpl,
      dateService,
      clientSideSessionFactory,
      config
    )
  }

  private def formWithValidDefaults(referenceNumber: String = ReferenceNumberValid,
                                    registrationNumber: String = RegistrationNumberValid,
                                    vehicleSoldTo: String = VehicleSoldTo_Private,
                                    sellerEmail: Option[String] = None,
                                    consent: String = ConsentValid) = {
    vehicleLookupResponseGenerator(vehicleDetailsResponseSuccess).form.bind(
      Map(
        DocumentReferenceNumberId -> referenceNumber,
        VehicleRegistrationNumberId -> registrationNumber,
        VehicleSoldToId -> vehicleSoldTo,
        s"$VehicleSellerEmail.$EmailId" -> sellerEmail.getOrElse(""),
        s"$VehicleSellerEmail.$EmailVerifyId" -> sellerEmail.getOrElse("")
      ) ++ sellerEmail.fold(Map(VehicleSellerEmailOption -> OptionalToggle.Invisible))
        (email => Map(VehicleSellerEmailOption -> OptionalToggle.Visible))
    )
  }
}
