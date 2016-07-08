package models

import mappings.Consent.regRight
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.Consent.consent
import uk.gov.dvla.vehicles.presentation.common.services.DateService

case class CompleteAndConfirmFormModel(regRight: String, consent: String)

object CompleteAndConfirmFormModel {
  implicit val JsonFormat = Json.format[CompleteAndConfirmFormModel]
  final val AllowGoingToCompleteAndConfirmPageCacheKey = s"${CookiePrefix}allowGoingToCompleteAndConfirmPage"
  final val CompleteAndConfirmCacheKey = s"${CookiePrefix}completeAndConfirm"
  final val CompleteAndConfirmCacheTransactionIdCacheKey = s"${CookiePrefix}completeAndConfirmTransactionId"
  implicit val Key = CacheKey[CompleteAndConfirmFormModel](CompleteAndConfirmCacheKey)

  object Form {
    final val RegRightId = "regRight"
    final val ConsentId = "consent"

    final def detailMapping(implicit dateService: DateService) = mapping(
      RegRightId -> regRight,
      ConsentId -> consent
    )(CompleteAndConfirmFormModel.apply)(CompleteAndConfirmFormModel.unapply)
  }
}
