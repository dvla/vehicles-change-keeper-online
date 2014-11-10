package constraints

import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.pattern

object BusinessKeeperName {

  final val Pattern = """^[a-zA-Z0-9][a-zA-Z0-9\s\-\'\,]*$"""

  def validBusinessKeeperName: Constraint[String] = pattern(
    regex = Pattern.r,
    name = "constraint.validBusinessKeeperName",
    error = "error.validBusinessKeeperName")
}