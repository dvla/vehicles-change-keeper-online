package models

import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

final case class ChangeKeeperCompletionViewModel(vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel,
                                                 newKeeperDetails: NewKeeperDetailsViewModel,
                                                 completeAndConfirmDetails: CompleteAndConfirmFormModel,
                                                 completeAndConfirmResponseModel: CompleteAndConfirmResponseModel)

object ChangeKeeperCompletionViewModel {
  implicit val JsonFormat = Json.format[CompleteAndConfirmResponseModel]
  final val AcquireCompletionCacheKey = "acquireCompletion"
  implicit val Key = CacheKey[CompleteAndConfirmResponseModel](AcquireCompletionCacheKey)
}