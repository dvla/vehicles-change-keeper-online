package models

import mappings.FleetNumber.fleetNumberMapping
import play.api.data.Forms.{mapping, optional}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.mappings.Email.email
import common.mappings.Postcode.postcode
import common.mappings.BusinessKeeperName.businessKeeperNameMapping

final case class BusinessKeeperDetailsFormModel(fleetNumber: Option[String],
                                                businessName: String,
                                                email: Option[String],
                                                postcode: String)

object BusinessKeeperDetailsFormModel {
  implicit val JsonFormat = Json.format[BusinessKeeperDetailsFormModel]
  final val BusinessKeeperDetailsCacheKey = "businessKeeperDetails"
  implicit val Key = CacheKey[BusinessKeeperDetailsFormModel](BusinessKeeperDetailsCacheKey)

  object Form {
    final val FleetNumberId = "fleetNumber"
    final val BusinessNameId = "businessName"
    final val EmailId = "businesskeeper_email"
    final val PostcodeId = "businesskeeper_postcode"

    final val Mapping = mapping(
      FleetNumberId -> fleetNumberMapping,
      BusinessNameId -> businessKeeperNameMapping,
      EmailId -> optional(email),
      PostcodeId -> postcode
    )(BusinessKeeperDetailsFormModel.apply)(BusinessKeeperDetailsFormModel.unapply)
  }
}