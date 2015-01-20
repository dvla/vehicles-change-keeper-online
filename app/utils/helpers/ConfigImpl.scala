package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getOptionalProperty, getProperty, getStringListProperty}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.{EmailConfiguration, From}

class ConfigImpl extends Config {

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
  val serviceTypeCode: String = getProperty[String]("webHeader.serviceTypeCode")
  val orgBusinessUnit: String = getProperty[String]("webHeader.orgBusinessUnit")


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
