package controllers.changeKeeper

import controllers.VehicleLookup
import helpers.UnitSpec
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import helpers.InvalidVRMFormat.allInvalidVrmFormats
import helpers.ValidVRMFormat.allValidVrmFormats
import play.api.http.Status.OK
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.services.DateServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionConfig
import common.webserviceclients.bruteforceprevention.BruteForcePreventionServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import models.VehicleLookupFormModel.Form.{DocumentReferenceNumberId, VehicleRegistrationNumberId, VehicleSoldToId}
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.when
import play.api.libs.json.{JsValue, Json}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsResponse
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.helpers.Config
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ConsentValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.vehicleDetailsResponseSuccess
import webserviceclients.fakes.{FakeDateServiceImpl, FakeResponse}
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private

final class VehicleLookupFormSpec extends UnitSpec {

  "form" should {
    "accept when all fields contain valid responses" in {
      formWithValidDefaults().get.referenceNumber should equal(ReferenceNumberValid)
      formWithValidDefaults().get.registrationNumber should equal(RegistrationNumberValid)
    }
  }

  "referenceNumber" should {
    allInvalidVrmFormats.map(vrm => "reject invalid vehicle registration mark : " + vrm in {
      formWithValidDefaults(registrationNumber = vrm).errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    })

    allValidVrmFormats.map(vrm => "accept valid vehicle registration mark : " + vrm in {
      formWithValidDefaults(registrationNumber = vrm).get.registrationNumber should equal(vrm)
    })

    "reject if blank" in {
      val vehicleLookupFormError = formWithValidDefaults(referenceNumber = "").errors
      val expectedKey = DocumentReferenceNumberId
      
      vehicleLookupFormError should have length 3
      vehicleLookupFormError(0).key should equal(expectedKey)
      vehicleLookupFormError(0).message should equal("error.minLength")
      vehicleLookupFormError(1).key should equal(expectedKey)
      vehicleLookupFormError(1).message should equal("error.required")
      vehicleLookupFormError(2).key should equal(expectedKey)
      vehicleLookupFormError(2).message should equal("error.restricted.validNumberOnly")
    }

    "reject if less than min length" in {
      formWithValidDefaults(referenceNumber = "1234567891").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if greater than max length" in {
      formWithValidDefaults(referenceNumber = "123456789101").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength")
    }

    "reject if contains letters" in {
      formWithValidDefaults(referenceNumber = "qwertyuiopl").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validNumberOnly")
    }

    "reject if contains special characters" in {
      formWithValidDefaults(referenceNumber = "£££££££££££").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validNumberOnly")
    }

    "accept if valid" in {
      formWithValidDefaults(registrationNumber = RegistrationNumberValid).get.referenceNumber should equal(ReferenceNumberValid)
    }
  }

  "registrationNumber" should {
    "reject if empty" in {
      formWithValidDefaults(registrationNumber = "").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.restricted.validVrnOnly")
    }

    "reject if less than min length" in {
      formWithValidDefaults(registrationNumber = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validVrnOnly")
    }

    "reject if more than max length" in {
      formWithValidDefaults(registrationNumber = "AB53WERT").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "reject if more than max length 2" in {
      formWithValidDefaults(registrationNumber = "PJ056YYY").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "reject if contains special characters" in {
      formWithValidDefaults(registrationNumber = "ab53ab%").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validVrnOnly")
    }

    "accept a selection of randomly generated vrms that all satisfy vrm regex" in {
      for (i <- 1 to 100) {
        val randomVrm = RandomVrmGenerator.vrm
        formWithValidDefaults(registrationNumber = randomVrm).get.registrationNumber should equal(randomVrm)
      }
    }
  }

  private val bruteForceServiceImpl: BruteForcePreventionService = {
    val bruteForcePreventionWebService: BruteForcePreventionWebService = mock[BruteForcePreventionWebService]
    when(bruteForcePreventionWebService.callBruteForce(anyString())).
      thenReturn( Future.successful( new FakeResponse(status = OK) ))

    new BruteForcePreventionServiceImpl(
      config = new BruteForcePreventionConfig,
      ws = bruteForcePreventionWebService,
      dateService = new FakeDateServiceImpl
    )
  }

  val dateService = new DateServiceImpl

  private def vehicleLookupResponseGenerator(fullResponse:(Int, Option[VehicleAndKeeperDetailsResponse])) = {
    val vehicleAndKeeperLookupWebService: VehicleAndKeeperLookupWebService = mock[VehicleAndKeeperLookupWebService]
    when(vehicleAndKeeperLookupWebService.invoke(any[VehicleAndKeeperDetailsRequest], any[String])).thenReturn(Future {
      val responseAsJson : Option[JsValue] = fullResponse._2 match {
        case Some(e) => Some(Json.toJson(e))
        case _ => None
      }
      new FakeResponse(status = fullResponse._1, fakeJson = responseAsJson)// Any call to a webservice will always return this successful response.
    })
    val vehicleAndKeeperLookupServiceImpl = new VehicleAndKeeperLookupServiceImpl(vehicleAndKeeperLookupWebService)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    new VehicleLookup(
      bruteForceService = bruteForceServiceImpl,
      vehicleLookupService = vehicleAndKeeperLookupServiceImpl, dateService
      )
  }

  private def formWithValidDefaults(referenceNumber: String = ReferenceNumberValid,
                                    registrationNumber: String = RegistrationNumberValid,
                                    vehicleSoldTo: String = VehicleSoldTo_Private,
                                    consent: String = ConsentValid
                                    ) = {
    vehicleLookupResponseGenerator(vehicleDetailsResponseSuccess).form.bind(
      Map(
        DocumentReferenceNumberId -> referenceNumber,
        VehicleRegistrationNumberId -> registrationNumber,
        VehicleSoldToId -> vehicleSoldTo
      )
    )
  }
}