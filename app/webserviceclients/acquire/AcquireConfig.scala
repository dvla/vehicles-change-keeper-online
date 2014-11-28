package webserviceclients.acquire

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty
import scala.concurrent.duration.DurationInt

class AcquireConfig {
  val baseUrl = getProperty("acquireVehicle.baseUrl", "NOT FOUND")
  val requestTimeout = getProperty("acquireVehicle.requestTimeout", 5.seconds.toMillis.toInt)
}
