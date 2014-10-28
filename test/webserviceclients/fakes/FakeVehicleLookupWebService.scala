package webserviceclients.fakes

import org.joda.time.DateTime
import play.api.http.Status.{OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehiclelookup.{VehicleDetailsDto, VehicleDetailsRequestDto, VehicleDetailsResponseDto, VehicleLookupWebService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class FakeVehicleLookupWebService extends VehicleLookupWebService {
  import webserviceclients.fakes.FakeVehicleLookupWebService._

  override def callVehicleLookupService(request: VehicleDetailsRequestDto, trackingId: String) = Future {
    val (responseStatus, response) = {
      request.referenceNumber match {
        case "99999999991" => vehicleDetailsResponseVRMNotFound
        case "99999999992" => vehicleDetailsResponseDocRefNumberNotLatest
        case "99999999993" => vehicleDetailsKeeperStillOnRecordResponseSuccess
        case "99999999999" => vehicleDetailsResponseNotFoundResponseCode
        case _ => vehicleDetailsResponseSuccess
      }
    }
    val responseAsJson = Json.toJson(response)
    new FakeResponse(status = responseStatus, fakeJson = Some(responseAsJson)) // Any call to a webservice will always return this successful response.
  }
}

object FakeVehicleLookupWebService {
  final val SoldToIndividual = ""
  final val RegistrationNumberValid = "AB12AWR"
  final val RegistrationNumberWithSpaceValid = "AB12 AWR"
  final val ReferenceNumberValid = "12345678910"
  final val VehicleMakeValid = "Alfa Romeo"
  final val VehicleModelValid = "Alfasud ti"
  final val KeeperNameValid = "Keeper Name"
  final val KeeperUprnValid = 10123456789L
  final val ConsentValid = "true"
  final val TransactionIdValid = "A1-100"
  final val VrmNotFound = "vehicle_lookup_vrm_not_found"
  final val DocumentRecordMismatch = "vehicle_lookup_document_record_mismatch"
  final val TransactionTimestampValid = new DateTime()

  private def vehicleDetails(disposeFlag: Boolean = true) =
    VehicleDetailsDto(
      registrationNumber = RegistrationNumberValid,
      vehicleMake = VehicleMakeValid,
      vehicleModel = VehicleModelValid,
      disposeFlag = disposeFlag
    )

  val vehicleDetailsResponseSuccess: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, Some(VehicleDetailsResponseDto(responseCode = None, vehicleDetailsDto = Some(vehicleDetails()))))
  }

  val vehicleDetailsKeeperStillOnRecordResponseSuccess: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, Some(VehicleDetailsResponseDto(responseCode = None, vehicleDetailsDto = Some(vehicleDetails(disposeFlag = false)))))
  }

  val vehicleDetailsResponseVRMNotFound: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, Some(VehicleDetailsResponseDto(responseCode = Some(VrmNotFound), vehicleDetailsDto = None)))
  }

  val vehicleDetailsResponseDocRefNumberNotLatest: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, Some(VehicleDetailsResponseDto(responseCode = Some(DocumentRecordMismatch), vehicleDetailsDto = None)))
  }

  val vehicleDetailsResponseNotFoundResponseCode: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, Some(VehicleDetailsResponseDto(responseCode = None, vehicleDetailsDto = None)))
  }

  val vehicleDetailsServerDown: (Int, Option[VehicleDetailsResponseDto]) = {
    (SERVICE_UNAVAILABLE, None)
  }

  val vehicleDetailsNoResponse: (Int, Option[VehicleDetailsResponseDto]) = {
    (OK, None)
  }
}