package composition

import uk.gov.dvla.vehicles.presentation.common.services.SEND.{From, EmailConfiguration}
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, booleanProp}

import utils.helpers.Config

/**
 * Created by gerasimosarvanitis on 07/01/2015.
 */
class TestConfig extends Config {

  // Prototype message in html
  def isPrototypeBannerVisible: Boolean = getOptionalProperty[Boolean]("prototype.disclaimer").getOrElse(true)

  // Google analytics
  def googleAnalyticsTrackingId: Option[String] = None

  // Progress step indicator
  def isProgressBarEnabled: Boolean = getOptionalProperty[Boolean]("progressBar.enabled").getOrElse(true)

  def isHtml5ValidationEnabled: Boolean =
    getOptionalProperty[Boolean]("html5Validation.enabled").getOrElse(false)

  def startUrl: String = "/before-you-start"

  def ordnanceSurveyUseUprn: Boolean = false

  // opening and closing times
  def opening: Int = 1
  def closing: Int = 18

  // Web headers
  def applicationCode: String = ""
  def serviceTypeCode: String = ""
  def orgBusinessUnit: String = ""
  def channelCode: String = ""
  def contactId: Long = 9


  def emailConfiguration: EmailConfiguration = EmailConfiguration(
    "",
    25,
    "",
    "",
    From("", "DO-NOT-REPLY"),
    From("", "Feedback"),
    None
  )

}
