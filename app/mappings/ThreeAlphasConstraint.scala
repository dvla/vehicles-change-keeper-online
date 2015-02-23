package mappings

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.dvla.vehicles.presentation.common.views.models.AddressAndPostcodeViewModel

object ThreeAlphasConstraint {

  val threeAlphasConstraint = Constraint[AddressAndPostcodeViewModel]("constraint.threealphas") {
    case model: AddressAndPostcodeViewModel =>
      if (model.addressLinesModel.buildingNameOrNumber.replaceAll( """[^A-Za-z]""", "").length < 3) 
        Invalid(ValidationError("error.threeAlphas"))
      else Valid
  }
}
