import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey

package object models {
  final val HelpCacheKey = "help"
  final val SeenCookieMessageCacheKey = "seen_cookie_message"

  // The full set of cache keys. These are removed at the start of the process in the "before_you_start" page
  final val AllCacheKeys = Set(VehicleLookupFormModelCacheKey)

  // Set of cookies related to all data entered for a private keeper
  final val PrivateKeeperDetailsCacheKeys = Set("fixMe")

  // Set of cookies related to all data entered for a business keeper
  final val BusinessKeeperDetailsCacheKeys = Set("fixMe")

}
