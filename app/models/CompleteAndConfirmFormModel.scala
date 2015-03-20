package models

import mappings.Consent.consent
import org.joda.time.LocalDate
import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.mappings.Date.{dateMapping, notInTheFuture, notBefore}
import uk.gov.dvla.vehicles.presentation.common.mappings.Mileage.mileage
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import models.K2KCacheKeyPrefix.CookiePrefix

case class CompleteAndConfirmFormModel(mileage: Option[Int],
                                       dateOfSale: LocalDate,
                                       consent: String)

object CompleteAndConfirmFormModel {
  implicit val JsonFormat = Json.format[CompleteAndConfirmFormModel]
  final val AllowGoingToCompleteAndConfirmPageCacheKey = s"${CookiePrefix}allowGoingToCompleteAndConfirmPage"
  final val CompleteAndConfirmCacheKey = s"${CookiePrefix}completeAndConfirm"
  final val CompleteAndConfirmCacheTransactionIdCacheKey = s"${CookiePrefix}completeAndConfirmTransactionId"
  implicit val Key = CacheKey[CompleteAndConfirmFormModel](CompleteAndConfirmCacheKey)

  object Form {
    final val MileageId = "mileage"
    final val DateOfSaleId = "dateofsale"
    final val TodaysDateId = "todays_date"
    final val ConsentId = "consent"


    final def detailMapping(implicit dateService: DateService) = mapping(
      MileageId -> mileage,
      DateOfSaleId -> dateMapping.verifying(notInTheFuture())
        .verifying(notBefore(dateService.now.toDateTime.toLocalDate.minusYears(5))),
      ConsentId -> consent
    )(CompleteAndConfirmFormModel.apply)(CompleteAndConfirmFormModel.unapply)
  }
}
