package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel

case class CompleteAndConfirmViewModel(form: Form[CompleteAndConfirmFormModel],
                                       vehicleDetails: VehicleDetailsModel,
                                       keeperDetails: NewKeeperDetailsViewModel)