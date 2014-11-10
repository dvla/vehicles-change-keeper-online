package mappings

import play.api.data.Mapping
import uk.gov.dvla.vehicles.presentation.common
import common.views.helpers.FormExtensions.nonEmptyTextWithTransform
import constraints.BusinessKeeperName.validBusinessKeeperName

object BusinessKeeperName {
  final val MinLength = 2
  final val MaxLength = 30

  def businessKeeperNameMapping: Mapping[String] =
    nonEmptyTextWithTransform(_.toUpperCase.trim)(MinLength, MaxLength) verifying validBusinessKeeperName
}