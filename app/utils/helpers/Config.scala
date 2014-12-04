package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

class Config {

  private val notFound = "NOT FOUND"

  // Prototype message in html
  val isPrototypeBannerVisible: Boolean = getProperty("prototype.disclaimer", default = true)

  // Google analytics
  val googleAnalyticsTrackingId: String = getProperty("googleAnalytics.id.changeKeeper", notFound)

  // Progress step indicator
  val isProgressBarEnabled: Boolean = getProperty("progressBar.enabled", default = false)

  val isHtml5ValidationEnabled: Boolean = getProperty("html5Validation.enabled", default = false)

  val startUrl: String = getProperty("start.page", default = "NOT FOUND")

  val ordnanceSurveyUseUprn: Boolean = getProperty("ordnancesurvey.useUprn", default = false)

  // opening and closing times
  val opening: Int = getProperty("openingTime", default = 1)
  val closing: Int = getProperty("closingTime", default = 23)

  // Web headers
  val applicationCode: String = getProperty("webHeader.applicationCode", notFound)
  val serviceTypeCode: String = getProperty("webHeader.serviceTypeCode", notFound)
  val orgBusinessUnit: String = getProperty("webHeader.orgBusinessUnit", notFound)

}
