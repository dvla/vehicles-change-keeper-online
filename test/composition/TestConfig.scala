package composition

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{booleanProp, getOptionalProperty, intProp}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From

import utils.helpers.Config

class TestConfig extends Config {

  override def assetsUrl: Option[String] = None

  // Prototype message in html
  override def isPrototypeBannerVisible: Boolean = getOptionalProperty[Boolean]("prototype.disclaimer").getOrElse(true)

  // Google analytics
  override def googleAnalyticsTrackingId: Option[String] = None

  // Progress step indicator
  override def isProgressBarEnabled: Boolean = getOptionalProperty[Boolean]("progressBar.enabled").getOrElse(true)

  override def isHtml5ValidationEnabled: Boolean =
    getOptionalProperty[Boolean]("html5Validation.enabled").getOrElse(false)

  override def startUrl: String = "/before-you-start"

  override def ordnanceSurveyUseUprn: Boolean = false

  // Opening and closing times
  override def opening: Int = getOptionalProperty[Int]("openingTime").getOrElse(0)
  override def closing: Int = getOptionalProperty[Int]("closingTime").getOrElse(24)
  override def closingWarnPeriodMins: Int = getOptionalProperty[Int]("closingWarnPeriodMins").getOrElse(0)

  // Web headers
  override def applicationCode: String = ""
  override def vssServiceTypeCode: String = ""
  override def dmsServiceTypeCode: String = ""
  override def orgBusinessUnit: String = ""
  override def channelCode: String = ""
  override def contactId: Long = 9

  override def emailServiceMicroServiceUrlBase: String = NotFound
  override def emailServiceMsRequestTimeout: Int = 10000

  override def emailConfiguration: EmailConfiguration = EmailConfiguration(
    From("", "DO-NOT-REPLY"),
    From("", "Feedback"),
    None
  )

  def imagesPath: String = ""

  override def surveyUrl: Option[String] = Some("test/survey/url")
}
