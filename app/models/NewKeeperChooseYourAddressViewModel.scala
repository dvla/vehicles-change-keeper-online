package models

import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.model.{VehicleAndKeeperDetailsModel, VehicleDetailsModel}

case class NewKeeperChooseYourAddressViewModel(form: Form[models.NewKeeperChooseYourAddressFormModel],
                                               vehicleDetails: VehicleAndKeeperDetailsModel)