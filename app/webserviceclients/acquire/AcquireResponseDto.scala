package webserviceclients.acquire

import play.api.libs.json.Json

final case class AcquireResponseDto(transactionId: String,
                                  registrationNumber: String,
                                  responseCode: Option[String] = None)

object AcquireResponseDto{
  implicit val JsonFormat = Json.format[AcquireResponseDto]
}
