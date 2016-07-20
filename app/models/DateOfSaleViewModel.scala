package models

import controllers.routes
import play.api.data.Form
import play.api.mvc.Call
import routes.DateOfSale
import uk.gov.dvla.vehicles.presentation.common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}

case class DateOfSaleViewModel(form: Form[DateOfSaleFormModel],
                               vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel,
                               keeperDetails: NewKeeperDetailsViewModel,
                               showDateOfSaleWarning: Boolean = false,
                               submitAction: Call = DateOfSale.submitWithDateCheck(),
                               disposalDate: Option[String] = None)
