package webserviceclients.fakes

import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.TrackingId
import common.webserviceclients.acquire.{AcquireRequestDto, AcquireResponse, AcquireResponseDto, AcquireWebService}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse

import scala.concurrent.Future

class FakeAcquireWebServiceImpl extends AcquireWebService {
  import webserviceclients.fakes.FakeAcquireWebServiceImpl.SimulateMicroServiceUnavailable
  import webserviceclients.fakes.FakeAcquireWebServiceImpl.SimulateSoapEndpointFailure
  import webserviceclients.fakes.FakeAcquireWebServiceImpl.acquireResponseSoapEndpointFailure
  import webserviceclients.fakes.FakeAcquireWebServiceImpl.acquireResponseSuccess

  override def callAcquireService(request: AcquireRequestDto, trackingId: TrackingId): Future[WSResponse] =
    Future.successful {
    val acquireResponse: AcquireResponseDto = {
      request.referenceNumber match {
        case SimulateMicroServiceUnavailable => throw new RuntimeException("simulateMicroServiceUnavailable")
        case SimulateSoapEndpointFailure => acquireResponseSoapEndpointFailure
        case _ => acquireResponseSuccess
      }
    }
    val responseAsJson = Json.toJson(acquireResponse)
    Logger.debug(s"FakeVehicleLookupWebService callVehicleLookupService with: $responseAsJson")
    // Any call to a webservice will always return this successful response.
    new FakeResponse(status = OK, fakeJson = Some(responseAsJson))
  }
}

object FakeAcquireWebServiceImpl {
  final val TransactionIdValid = "1234"
  //private final val AuditIdValid = "7575"
  private final val SimulateMicroServiceUnavailable = "8" * 11
  private final val SimulateSoapEndpointFailure = "9" * 11
  private final val RegistrationNumberValid = "AB12AWR"

  val acquireResponseSuccess = AcquireResponseDto(
    None,
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = RegistrationNumberValid)
  )

  // We should always get back a transaction id even for failure scenarios.
  // Only exception is if the soap endpoint is down
  val acquireResponseGeneralError = AcquireResponseDto(
    Some(MicroserviceResponse("U0020", "ms.vehiclesService.error.generalError")),
    AcquireResponse(transactionId = TransactionIdValid, registrationNumber = "AA11AAC")
  )

  // No transactionId because the soap endpoint is down
  val acquireResponseSoapEndpointFailure = AcquireResponseDto(
    None,
    AcquireResponse(transactionId = "", registrationNumber = "")
  )

  final val ConsentValid = "true"
  final val MileageValid = "20000"
  final val MileageInvalid = "INVALID"
}
