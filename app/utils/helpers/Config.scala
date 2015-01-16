package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getProperty, getOptionalProperty, getStringListProperty}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.{From, EmailConfiguration}

class Config {

  private lazy val notFound = "NOT FOUND"

  // Prototype message in html
  lazy val isPrototypeBannerVisible: Boolean = getProperty[Boolean]("prototype.disclaimer")

  lazy val isProgressBarEnabled: Boolean = getProperty[Boolean]("progressBar.enabled")

  lazy val startUrl: String = getProperty[String]("start.page")

  // Google analytics
  lazy val googleAnalyticsTrackingId: Option[String] = getOptionalProperty[String]("googleAnalytics.id.changeKeeper")
  // Progress step indicator

  lazy val isHtml5ValidationEnabled: Boolean = getProperty[Boolean]("html5Validation.enabled")


  lazy val ordnanceSurveyUseUprn: Boolean = getProperty[Boolean]("ordnancesurvey.useUprn")

  // opening and closing times
  lazy val opening: Int = getProperty[Int]("openingTime")
  lazy val closing: Int = getProperty[Int]("closingTime")

  // Web headers
  lazy val applicationCode: String = getOptionalProperty[String]("webHeader.applicationCode").getOrElse(notFound)
  lazy val serviceTypeCode: String = getOptionalProperty[String]("webHeader.serviceTypeCode").getOrElse(notFound)
  lazy val orgBusinessUnit: String = getOptionalProperty[String]("webHeader.orgBusinessUnit").getOrElse(notFound)


  lazy val emailConfiguration: EmailConfiguration = EmailConfiguration(
    getOptionalProperty[String]("smtp.host").getOrElse(""),
    getOptionalProperty[Int]("smtp.port").getOrElse(25),
    getOptionalProperty[String]("smtp.user").getOrElse(""),
    getOptionalProperty[String]("smtp.password").getOrElse(""),
    From(getOptionalProperty[String]("email.senderAddress").getOrElse(""), "DO-NOT-REPLY"),
    From(getOptionalProperty[String]("email.feedbackAddress").getOrElse(""), "Feedback"),
    getStringListProperty("email.whitelist")
  )

}
