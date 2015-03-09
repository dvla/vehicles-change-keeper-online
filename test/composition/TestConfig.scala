package composition

import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, booleanProp}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From

import utils.helpers.Config

class TestConfig extends Config {

  override def assetsUrl: Option[String] = None

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
  def vssServiceTypeCode: String = ""
  def dmsServiceTypeCode: String = ""
  def orgBusinessUnit: String = ""
  def channelCode: String = ""
  def contactId: Long = 9

  def emailServiceMicroServiceUrlBase: String = NotFound
  def emailServiceMsRequestTimeout: Int = 10000

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
