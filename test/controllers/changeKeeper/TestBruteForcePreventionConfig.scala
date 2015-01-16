package controllers.changeKeeper

import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention.BruteForcePreventionConfig
import scala.concurrent.duration.DurationInt

/**
 * Created by gerasimosarvanitis on 09/01/2015.
 */
class TestBruteForcePreventionConfig extends BruteForcePreventionConfig {

  override lazy val baseUrl: String = "NOT FOUND"
  override lazy val requestTimeoutMillis: Int = 5.seconds.toMillis.toInt
  override lazy val isEnabled: Boolean = true
  override lazy val nameHeader: String = ""
  override lazy val maxAttemptsHeader: Int = 3
  override lazy val expiryHeader: String = ""

}
