package webserviceclients.acquire

import play.api.libs.json.Json

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

final case class AcquireRequestDto(referenceNumber: String,
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
  implicit val JsonFormat = Json.writes[AcquireRequestDto]
}


