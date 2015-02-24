package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperChooseYourAddressFormModel

case class NewKeeperChooseYourAddressViewModel(form: Form[uk.gov.dvla.vehicles.presentation.common.model.NewKeeperChooseYourAddressFormModel],
                                               vehicleDetails: VehicleAndKeeperDetailsModel)