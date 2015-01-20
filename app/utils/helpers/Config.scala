package utils.helpers

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.{getProperty, getOptionalProperty, getStringListProperty}
import uk.gov.dvla.vehicles.presentation.common.services.SEND.{From, EmailConfiguration}

trait Config {

  // Prototype message in html
  def isPrototypeBannerVisible: Boolean

  def isProgressBarEnabled: Boolean

  def startUrl: String

  // Google analytics
  def googleAnalyticsTrackingId: Option[String]
  // Progress step indicator

  def isHtml5ValidationEnabled: Boolean


  def ordnanceSurveyUseUprn: Boolean

  // opening and closing times
  def opening: Int
  def closing: Int

  // Web headers
  def applicationCode: String
  def serviceTypeCode: String
  def orgBusinessUnit: String
  def channelCode: String
  def contactId: Long


  def emailConfiguration: EmailConfiguration

}
