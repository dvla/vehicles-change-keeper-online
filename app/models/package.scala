import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.DateOfSaleFormModel.DateOfSaleCacheKey
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}
import uk.gov.dvla.vehicles.presentation.common
import common.model.BruteForcePreventionModel.bruteForcePreventionViewModelCacheKey
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
import common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import common.model.NewKeeperEnterAddressManuallyFormModel.newKeeperEnterAddressManuallyCacheKey
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey

package object models {
  final val HelpCacheKey = s"${CookiePrefix}help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message" // Same value across all exemplars
  final val IdentifierCacheKey = s"${CookiePrefix}identifier"

  final val ChangeKeeperCacheKeys = Set(
    newKeeperChooseYourAddressCacheKey,
    bruteForcePreventionViewModelCacheKey
  )

  final val VehicleDetailsCacheKeys = Set(
    VehicleLookupFormModelCacheKey,
    vehicleAndKeeperLookupDetailsCacheKey
  )

  final val PrivateKeeperDetailsCacheKeys = Set(
    privateKeeperDetailsCacheKey,
    newKeeperDetailsCacheKey,
    newKeeperChooseYourAddressCacheKey,
    newKeeperEnterAddressManuallyCacheKey
  )

  final val BusinessKeeperDetailsCacheKeys = Set(
    businessKeeperDetailsCacheKey,
    newKeeperDetailsCacheKey,
    newKeeperChooseYourAddressCacheKey,
    newKeeperEnterAddressManuallyCacheKey
  )

  final val VehicleNewKeeperCompletionCacheKeys =
    ChangeKeeperCacheKeys
      .++(VehicleDetailsCacheKeys)
      .++(PrivateKeeperDetailsCacheKeys)
      .++(BusinessKeeperDetailsCacheKeys)
      .+(HelpCacheKey)

  final val CompletionCacheKeys = Set(
    newKeeperDetailsCacheKey,
    DateOfSaleCacheKey,
    CompleteAndConfirmCacheKey,
    VehicleLookupResponseCodeCacheKey,
    ChangeKeeperCompletionResponseCacheKey
  )

  final val AllCacheKeys =
    VehicleDetailsCacheKeys
      .++(PrivateKeeperDetailsCacheKeys)
      .++(BusinessKeeperDetailsCacheKeys)
      .++(CompletionCacheKeys)
      .+(IdentifierCacheKey)
}
