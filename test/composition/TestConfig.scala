package composition

import uk.gov.dvla.vehicles.presentation.common.services.SEND.{From, EmailConfiguration}
import utils.helpers.Config

/**
 * Created by gerasimosarvanitis on 07/01/2015.
 */
object TestConfig extends Config {

  // Prototype message in html
  override lazy val isPrototypeBannerVisible: Boolean = true

  // Google analytics
  override lazy val googleAnalyticsTrackingId: Option[String] = None

  // Progress step indicator
  override lazy val isProgressBarEnabled: Boolean = false

  override lazy val isHtml5ValidationEnabled: Boolean = false

  override lazy val startUrl: String = "/before-you-start"

  override lazy val ordnanceSurveyUseUprn: Boolean = false

  // opening and closing times
  override lazy val opening: Int = 1
  override lazy val closing: Int = 18

  // Web headers
  override lazy val applicationCode: String = ""
  override lazy val serviceTypeCode: String = ""
  override lazy val orgBusinessUnit: String = ""


  override lazy val emailConfiguration: EmailConfiguration = EmailConfiguration(
    "",
    25,
    "",
    "",
    From("", "DO-NOT-REPLY"),
    From("", "Feedback"),
    None
  )

}
