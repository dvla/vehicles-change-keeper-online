package utils.helpers

import uk.gov.dvla.vehicles.presentation.common
import common.ConfigProperties.getOptionalProperty
import common.ConfigProperties.getProperty
import common.ConfigProperties.getStringListProperty
import common.ConfigProperties.booleanProp
import common.ConfigProperties.stringProp
import common.ConfigProperties.intProp
import common.ConfigProperties.longProp
import common.services.SEND.EmailConfiguration
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.From

class ConfigImpl extends Config {

  override def assetsUrl: Option[String] = getOptionalProperty[String]("assets.url")

  // Prototype message in html
  val isPrototypeBannerVisible: Boolean = getProperty[Boolean]("prototype.disclaimer")

  val isProgressBarEnabled: Boolean = getProperty[Boolean]("progressBar.enabled")

  val startUrl: String = getProperty[String]("start.page")

  // Google analytics
  val googleAnalyticsTrackingId: Option[String] = getOptionalProperty[String]("googleAnalytics.id.changeKeeper")
  // Progress step indicator

  val isHtml5ValidationEnabled: Boolean = getProperty[Boolean]("html5Validation.enabled")


  val ordnanceSurveyUseUprn: Boolean = getProperty[Boolean]("ordnancesurvey.useUprn")

  // opening and closing times
  val opening: Int = getProperty[Int]("openingTime")
  val closing: Int = getProperty[Int]("closingTime")

  // Web headers
  val applicationCode: String = getProperty[String]("webHeader.applicationCode")
  val vssServiceTypeCode: String = getProperty[String]("webHeader.vssServiceTypeCode")
  val dmsServiceTypeCode: String = getProperty[String]("webHeader.dmsServiceTypeCode")
  val orgBusinessUnit: String = getProperty[String]("webHeader.orgBusinessUnit")
  val channelCode: String = getProperty[String]("webHeader.channelCode")
  val contactId: Long = getProperty[Long]("webHeader.contactId")


  override val emailServiceMicroServiceUrlBase: String =
    getOptionalProperty[String]("emailServiceMicroServiceUrlBase").getOrElse(NotFound)
  override val emailServiceMsRequestTimeout: Int =
    getOptionalProperty[Int]("emailService.ms.requesttimeout").getOrElse(10000)

  val emailConfiguration: EmailConfiguration = EmailConfiguration(
    getProperty[String]("smtp.host"),
    getProperty[Int]("smtp.port"),
    getProperty[String]("smtp.user"),
    getProperty[String]("smtp.password"),
    From(getProperty[String]("email.senderAddress"), "DO-NOT-REPLY"),
    From(getProperty[String]("email.feedbackAddress"), "Feedback"),
    getStringListProperty("email.whitelist")
  )

}
