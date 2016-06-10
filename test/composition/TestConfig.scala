package composition

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{booleanProp, getOptionalProperty, intProp}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From
import utils.helpers.Config

class TestConfig extends Config {

  override val assetsUrl: Option[String] = None

  // Prototype message in html
  override val isPrototypeBannerVisible: Boolean = getOptionalProperty[Boolean]("prototype.disclaimer").getOrElse(TestConfig.DEFAULT_PB_VISIBLE)

  // Google analytics
  override val googleAnalyticsTrackingId: Option[String] = None

  override val isHtml5ValidationEnabled: Boolean =
    getOptionalProperty[Boolean]("html5Validation.enabled").getOrElse(TestConfig.DEFAULT_HTML_VALIDATION)

  override val startUrl: String = TestConfig.START_URL

  // Opening and closing times
  def openingTimeMinOfDay: Int = getOptionalProperty[Int]("openingTimeMinOfDay").getOrElse(TestConfig.DEFAULT_OPENING_TIME)
  def closingTimeMinOfDay: Int = getOptionalProperty[Int]("closingTimeMinOfDay").getOrElse(TestConfig.DEFAULT_CLOSING_TIME)
  override val closingWarnPeriodMins: Int = getOptionalProperty[Int]("closingWarnPeriodMins").getOrElse(TestConfig.DEFAULT_CLOSING_WARN_PERIOD)

  // Web headers
  override val applicationCode = TestConfig.WEB_APPLICATION_CODE
  override val vssServiceTypeCode = TestConfig.WEB_VSSSERVICETYPE_CODE
  override val dmsServiceTypeCode = TestConfig.WEB_DMSSERVICETYPE_CODE
  override val channelCode = TestConfig.WEB_CHANNEL_CODE
  override val contactId = TestConfig.WEB_CONTACT_ID
  override val orgBusinessUnit = TestConfig.WEB_ORG_BU

  override val emailServiceMicroServiceUrlBase: String = TestConfig.NotFound
  override val emailServiceMsRequestTimeout: Int = TestConfig.EMAIL_REQUEST_TIMEOUT

  override val emailConfiguration: EmailConfiguration = EmailConfiguration(
    from = From(TestConfig.EMAIL_FROM_EMAIL, TestConfig.EMAIL_FROM_NAME),
    feedbackEmail = From(TestConfig.EMAILFEEDBACK_FROM_EMAIL, TestConfig.EMAILFEEDBACK_FROM_NAME),
    whiteList = None
  )

  override val imagesPath: String = TestConfig.IMAGES_PATH

//  override val surveyUrl: Option[String] = Some("test/survey/url")
  override val surveyUrl: Option[String] = None
}

// placeholder for defaults and fixed test data
object TestConfig {
  final val NotFound = "NOT FOUND"

  final val EMAIL_FROM_NAME = "Someone"
  final val EMAILFEEDBACK_FROM_NAME = "Nobody"
  final val EMAIL_FROM_EMAIL = ""
  final val EMAILFEEDBACK_FROM_EMAIL = ""

  final val WEB_HEADER_TESTVAL = "WEBVOA"
  final val WEB_APPLICATION_CODE = WEB_HEADER_TESTVAL
  final val WEB_VSSSERVICETYPE_CODE = WEB_HEADER_TESTVAL
  final val WEB_DMSSERVICETYPE_CODE = "X"
  final val WEB_CHANNEL_CODE = WEB_HEADER_TESTVAL
  final val WEB_CONTACT_ID = 1L
  final val WEB_ORG_BU = WEB_HEADER_TESTVAL

  final val VERY_LONG_SURVEY_INTERVAL = 1000000000000L // in millis (approx 11574 days!)

  final val START_URL = "/before-you-start"
  final val EMPTY_URL = ""
  final val IMAGES_PATH = ""
  final val EMAIL_REQUEST_TIMEOUT = 10000

  // defaults
  final val DEFAULT_HTML_VALIDATION = false
  final val DEFAULT_PB_VISIBLE = true
  final val DEFAULT_OPENING_TIME = 0
  final val DEFAULT_CLOSING_TIME = 1440
  final val DEFAULT_CLOSING_WARN_PERIOD = 0

}