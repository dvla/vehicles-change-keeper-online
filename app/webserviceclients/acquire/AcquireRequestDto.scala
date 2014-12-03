package webserviceclients.acquire

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.{VssWebEndUserDto, VssWebHeaderDto}

case class TitleTypeDto(titleType: Option[Int], other: Option[String])

object TitleTypeDto{
  implicit val JsonFormat = Json.writes[TitleTypeDto]
}

final case class KeeperDetailsDto(keeperTitle: TitleTypeDto,
                               KeeperBusinessName: Option[String],
                               keeperForename: Option[String],
                               keeperSurname: Option[String],
                               keeperDateOfBirth: Option[String] = None,
                               keeperAddressLines: Seq[String],
                               keeperPostTown: String,
                               keeperPostCode: String,
                               keeperEmailAddress: Option[String],
                               keeperDriverNumber: Option[String])

object KeeperDetailsDto{
  implicit val JsonFormat = Json.writes[KeeperDetailsDto]
}

final case class TraderDetailsDto(traderOrganisationName: String,
                               traderAddressLines: Seq[String],
                               traderPostTown: String,
                               traderPostCode: String,
                               traderEmailAddress: Option[String])

object TraderDetailsDto{
  implicit val JsonFormat = Json.writes[TraderDetailsDto]
}

final case class AcquireRequestDto(webHeader: VssWebHeaderDto,
                                   referenceNumber: String,
                                   registrationNumber: String,
                                   keeperDetails: KeeperDetailsDto,
                                   traderDetails: Option[TraderDetailsDto],
                                   fleetNumber: Option[String] = None,
                                   dateOfTransfer: String,
                                   mileage: Option[Int],
                                   keeperConsent: Boolean,
                                   transactionTimestamp: String,
                                   requiresSorn: Boolean = false)

object AcquireRequestDto {

  // Handles this type of formatted string 2014-03-04T00:00:00.000Z
  implicit val jodaISODateWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(dateTime: DateTime): JsValue = {
      val formatter = ISODateTimeFormat.dateTime
      JsString(formatter.print(dateTime))
    }
  }

  implicit val JsonFormatVssWebEndUserDto = Json.writes[VssWebEndUserDto]
  implicit val JsonFormatVssWebHeaderDto = Json.writes[VssWebHeaderDto]
  implicit val JsonFormat = Json.writes[AcquireRequestDto]
}


