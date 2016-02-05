package webserviceclients.fakes

import org.joda.time.DateTime
import play.api.http.Status.{NOT_FOUND, OK, SERVICE_UNAVAILABLE}
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.TrackingId
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup
import vehicleandkeeperlookup.VehicleAndKeeperLookupFailureResponse
import vehicleandkeeperlookup.VehicleAndKeeperLookupDetailsDto
import vehicleandkeeperlookup.VehicleAndKeeperLookupRequest
import vehicleandkeeperlookup.VehicleAndKeeperLookupSuccessResponse
import vehicleandkeeperlookup.VehicleAndKeeperLookupWebService

final class FakeVehicleAndKeeperLookupWebService extends VehicleAndKeeperLookupWebService {
  import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService._

  override def invoke(request: VehicleAndKeeperLookupRequest, trackingId: TrackingId) = Future {
    val (responseStatus: Int, response: JsValue) = {
      request.referenceNumber match {
        case "99999999991" => (vehicleDetailsResponseVRMNotFound._1,
                               Json.toJson(vehicleDetailsResponseVRMNotFound._2.get.left.get))
        case "99999999992" => (vehicleDetailsResponseDocRefNumberNotLatest._1,
                               Json.toJson(vehicleDetailsResponseDocRefNumberNotLatest._2.get.left.get))
        case "99999999993" => (vehicleDetailsKeeperStillOnRecordResponseSuccess._1,
                               Json.toJson(vehicleDetailsKeeperStillOnRecordResponseSuccess._2.get.right.get))
        case "99999999999" => (vehicleDetailsResponseNotFoundResponseCode._1,
                               Json.toJson(vehicleDetailsResponseNotFoundResponseCode._2.get.right.get))
        case _ => (vehicleDetailsResponseSuccess._1, Json.toJson(vehicleDetailsResponseSuccess._2.get.right.get))
      }
    }
    new FakeResponse(status = responseStatus, fakeJson = Some(response))
  }
}

object FakeVehicleAndKeeperLookupWebService {
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
  final val VrmNotFound = MicroserviceResponse(code = "", message = "vehicle_lookup_vrm_not_found")
  final val DocumentRecordMismatch = MicroserviceResponse(code = "", message = "vehicle_lookup_document_record_mismatch")
  final val TransactionTimestampValid = new DateTime()
  final val UnhandledException = "unhandled_exception"

  private def vehicleDetails(disposeFlag: Boolean = true) =
    VehicleAndKeeperLookupDetailsDto(
      registrationNumber = RegistrationNumberValid,
      vehicleMake = Some(VehicleMakeValid),
      vehicleModel = Some(VehicleModelValid),
      keeperTitle = Some("a"),
      keeperFirstName = Some("a"),
      keeperLastName = Some("a"),
      keeperAddressLine1 = Some("a"),
      keeperAddressLine2 = Some("a"),
      keeperAddressLine3 = Some("a"),
      keeperAddressLine4 = Some("a"),
      keeperPostTown = Some("a"),
      keeperPostcode = Some("a"),
      disposeFlag = Some(disposeFlag),
      keeperEndDate = if (disposeFlag) Some(new DateTime()) else None,
      keeperChangeDate = None,
      suppressedV5Flag = None
    )

  val vehicleDetailsResponseSuccess: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                         VehicleAndKeeperLookupSuccessResponse]]) =
    (OK, Some(Right(VehicleAndKeeperLookupSuccessResponse(Some(vehicleDetails())))))

  val vehicleDetailsKeeperStillOnRecordResponseSuccess: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                                            VehicleAndKeeperLookupSuccessResponse]]) =
    (OK, Some(Right(VehicleAndKeeperLookupSuccessResponse(Some(vehicleDetails(disposeFlag = false))))))

  val vehicleDetailsResponseNotFoundResponseCode: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                                      VehicleAndKeeperLookupSuccessResponse]]) =
    (OK, Some(Right(VehicleAndKeeperLookupSuccessResponse(None))))

  val vehicleDetailsResponseVRMNotFound: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                             VehicleAndKeeperLookupSuccessResponse]]) =
    (NOT_FOUND, Some(Left(VehicleAndKeeperLookupFailureResponse(VrmNotFound))))

  val vehicleDetailsResponseDocRefNumberNotLatest: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                                       VehicleAndKeeperLookupSuccessResponse]]) =
    (NOT_FOUND, Some(Left(VehicleAndKeeperLookupFailureResponse(DocumentRecordMismatch))))

  val vehicleDetailsResponseUnhandledException: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                                    VehicleAndKeeperLookupSuccessResponse]]) =
    (NOT_FOUND, Some(Left(VehicleAndKeeperLookupFailureResponse(DocumentRecordMismatch))))

  val vehicleDetailsServerDown: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                    VehicleAndKeeperLookupSuccessResponse]]) =
    (SERVICE_UNAVAILABLE, None)

  val vehicleDetailsNoResponse: (Int, Option[Either[VehicleAndKeeperLookupFailureResponse,
                                                    VehicleAndKeeperLookupSuccessResponse]]) =
    (OK, None)
}