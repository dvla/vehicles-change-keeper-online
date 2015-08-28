package models

import models.K2KCacheKeyPrefix.CookiePrefix
import org.joda.time.DateTime
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey

final case class CompleteAndConfirmResponseModel(transactionId: String,
                                                 transactionTimestamp: DateTime)

object CompleteAndConfirmResponseModel {
  implicit val JsonFormat = Json.format[CompleteAndConfirmResponseModel]
  final val ChangeKeeperCompletionResponseCacheKey = s"${CookiePrefix}acquireCompletionResponse"
  implicit val Key = CacheKey[CompleteAndConfirmResponseModel](ChangeKeeperCompletionResponseCacheKey)
}
