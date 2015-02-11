package models

import play.api.data.Forms._
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.mappings.Email.email
import common.mappings.{VehicleRegistrationNumber, DocumentReferenceNumber}

final case class VehicleLookupFormModel(referenceNumber: String,
                                        registrationNumber: String,
                                        vehicleSoldTo: String,
                                         sellerEmail: Option[String] = None)

object VehicleLookupFormModel {
  implicit val JsonFormat = Json.format[VehicleLookupFormModel]
  final val VehicleLookupFormModelCacheKey = s"${CookiePrefix}vehicleLookupFormModel"
  implicit val Key = CacheKey[VehicleLookupFormModel](VehicleLookupFormModelCacheKey)
  final val VehicleLookupResponseCodeCacheKey = s"${CookiePrefix}vehicleLookupResponseCode"

  object Form {
    final val DocumentReferenceNumberId = "documentReferenceNumber"
    final val VehicleRegistrationNumberId = "vehicleRegistrationNumber"
    final val VehicleSoldToId = "vehicleSoldTo"
    final val VehicleSellerEmail = "vehicleSellerEmail"


    final val Mapping = mapping(
      DocumentReferenceNumberId -> DocumentReferenceNumber.referenceNumber,
      VehicleRegistrationNumberId -> VehicleRegistrationNumber.registrationNumber,
      VehicleSoldToId -> nonEmptyText,
      VehicleSellerEmail -> optional(email)
    )(VehicleLookupFormModel.apply)(VehicleLookupFormModel.unapply)
  }
}