import models.VehicleLookupFormModel._
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import models.BusinessKeeperDetailsFormModel.BusinessKeeperDetailsCacheKey
import models.NewKeeperEnterAddressManuallyFormModel.NewKeeperEnterAddressManuallyCacheKey
import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel._
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey

package object models {
  final val HelpCacheKey = "help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message"

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
    BusinessKeeperDetailsCacheKey,
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