package models

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import models.K2KCacheKeyPrefix.CookiePrefix


final case class SellerEmailModel(email: Option[String])

object SellerEmailModel {
  implicit val JsonFormat = Json.format[SellerEmailModel]
  final val SellerEmailModelCacheKey = s"${CookiePrefix}sellerEmailModel"
  implicit val Key = CacheKey[SellerEmailModel](SellerEmailModelCacheKey)
}
