package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperDetailsViewModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

case class CompleteAndConfirmViewModel(form: Form[CompleteAndConfirmFormModel],
                                       vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel,
                                       keeperDetails: NewKeeperDetailsViewModel,
                                       dateOfSaleModel: DateOfSaleFormModel)
