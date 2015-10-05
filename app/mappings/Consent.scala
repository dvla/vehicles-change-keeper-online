package mappings

import play.api.data.Forms.nonEmptyText
import play.api.data.Mapping

object Consent {
  def regRight: Mapping[String] = nonEmptyText
  def consent: Mapping[String] = nonEmptyText
}