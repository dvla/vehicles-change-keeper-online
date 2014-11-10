package mappings

import play.api.data.Forms.{optional, text}
import play.api.data.Mapping
import constraints.FleetNumber.fleetNumber

object FleetNumber {
  def fleetNumberMapping: Mapping[Option[String]] = optional(text verifying fleetNumber)
}
