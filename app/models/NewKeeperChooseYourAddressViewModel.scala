package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperChooseYourAddressFormModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

case class NewKeeperChooseYourAddressViewModel(form: Form[NewKeeperChooseYourAddressFormModel],
                                               vehicleDetails: VehicleAndKeeperDetailsModel)