package models

import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.data.Forms.{mapping, nonEmptyText, optional}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.controllers.VehicleLookupFormModelBase
import common.mappings.DocumentReferenceNumber
import common.mappings.Email.email
import common.mappings.VehicleRegistrationNumber

final case class VehicleLookupFormModel(referenceNumber: String,
                                        registrationNumber: String,
                                        vehicleSoldTo: String,
                                        sellerEmail: Option[String] = None) extends VehicleLookupFormModelBase

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
