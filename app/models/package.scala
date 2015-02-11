import models.VehicleLookupFormModel._
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import models.NewKeeperEnterAddressManuallyFormModel.NewKeeperEnterAddressManuallyCacheKey
import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel._
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey

package object models {
  implicit final val CookiePrefix = CacheKeyPrefix("k2k-")
  final val HelpCacheKey = s"${CookiePrefix}help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message" // Same value across all exemplars

  final val ChangeKeeperCacheKeys = Set(
    NewKeeperChooseYourAddressCacheKey,
    BruteForcePreventionViewModelCacheKey
  )

  final val VehicleDetailsCacheKeys = Set(
    VehicleLookupFormModelCacheKey,
    VehicleAndKeeperLookupDetailsCacheKey
  )

  final val PrivateKeeperDetailsCacheKeys = Set(
    PrivateKeeperDetailsCacheKey,
    NewKeeperDetailsCacheKey,
    NewKeeperChooseYourAddressCacheKey,
    NewKeeperEnterAddressManuallyCacheKey
  )

  final val BusinessKeeperDetailsCacheKeys = Set(
    businessKeeperDetailsCacheKey,
    NewKeeperDetailsCacheKey,
    NewKeeperChooseYourAddressCacheKey,
    NewKeeperEnterAddressManuallyCacheKey
  )

  final val VehicleNewKeeperCompletionCacheKeys =
    ChangeKeeperCacheKeys
      .++(VehicleDetailsCacheKeys)
      .++(PrivateKeeperDetailsCacheKeys)
      .++(BusinessKeeperDetailsCacheKeys)
      .++(Set(HelpCacheKey))

  final val CompletionCacheKeys = Set(
    NewKeeperDetailsCacheKey,
    CompleteAndConfirmCacheKey,
    VehicleLookupResponseCodeCacheKey,
    ChangeKeeperCompletionResponseCacheKey
  )

  final val AllCacheKeys =
    VehicleDetailsCacheKeys
      .++(PrivateKeeperDetailsCacheKeys)
      .++(BusinessKeeperDetailsCacheKeys)
      .++(CompletionCacheKeys)
}
