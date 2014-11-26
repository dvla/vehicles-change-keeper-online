package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.{VehicleAndKeeperDetailsModel, VehicleDetailsModel}

case class CompleteAndConfirmViewModel(form: Form[CompleteAndConfirmFormModel],
                                       vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel,
                                       keeperDetails: NewKeeperDetailsViewModel)