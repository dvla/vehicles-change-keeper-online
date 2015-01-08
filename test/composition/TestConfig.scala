package composition

import uk.gov.dvla.vehicles.presentation.common.services.SEND.{From, EmailConfiguration}
import utils.helpers.Config

/**
 * Created by gerasimosarvanitis on 07/01/2015.
 */
object TestConfig extends Config {

  // Prototype message in html
  override val isPrototypeBannerVisible: Boolean = true

  // Google analytics
  override val googleAnalyticsTrackingId: Option[String] = None

  // Progress step indicator
  override val isProgressBarEnabled: Boolean = false

  override val isHtml5ValidationEnabled: Boolean = false

  override val startUrl: String = "/before-you-start"

  override val ordnanceSurveyUseUprn: Boolean = false

  // opening and closing times
  override val opening: Int = 1
  override val closing: Int = 18

  // Web headers
  override val applicationCode: String = ""
  override val serviceTypeCode: String = ""
  override val orgBusinessUnit: String = ""


  override val emailConfiguration: EmailConfiguration = EmailConfiguration(
    "",
    25,
    "",
    "",
    From("", "DO-NOT-REPLY"),
    From("", "Feedback"),
    None
  )

}
