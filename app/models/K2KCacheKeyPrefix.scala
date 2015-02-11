package models

import uk.gov.dvla.vehicles.presentation.common.model.CacheKeyPrefix

object K2KCacheKeyPrefix {
  implicit final val CookiePrefix = CacheKeyPrefix("k2k-")
}
