package constraints

import play.api.data.validation.Constraints.pattern

object FleetNumber {
  final val MinLength = 6
  final val MaxLength = 6
  final val Pattern = """^[0-9]{6}|[0-9]{5}\-$"""

  val fleetNumber = pattern(
    regex = Pattern.r,
    name = "constraint.fleetNumber",
    error = "error.fleetNumber"
  )
}