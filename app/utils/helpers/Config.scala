package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupConfig
import uk.gov.dvla.vehicles.presentation.common.services.SEND.EmailConfiguration

trait Config extends VehicleLookupConfig {

  final val NotFound = "NOT FOUND"

  def assetsUrl: Option[String]

  // Prototype message in html
  def isPrototypeBannerVisible: Boolean

  // Progress step indicator
  def isProgressBarEnabled: Boolean

  def startUrl: String

  // Google analytics
  def googleAnalyticsTrackingId: Option[String]

  def isHtml5ValidationEnabled: Boolean

  def ordnanceSurveyUseUprn: Boolean

  // Opening and closing times
  def opening: Int
  def closing: Int
  def closingWarnPeriodMins: Int

  def emailServiceMicroServiceUrlBase: String
  def emailServiceMsRequestTimeout: Int

  def emailConfiguration: EmailConfiguration
}
