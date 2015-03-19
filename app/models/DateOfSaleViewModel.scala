package models

import controllers.routes
import play.api.data.Form
import play.api.mvc.Call
import uk.gov.dvla.vehicles.presentation.common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}
import routes.DateOfSale

case class DateOfSaleViewModel(form: Form[DateOfSaleFormModel],
                               vehicleAndKeeperDetails: VehicleAndKeeperDetailsModel,
                               keeperDetails: NewKeeperDetailsViewModel,
                               isSaleDateInvalid: Boolean,
                               isDateToCompareDisposalDate: Boolean,
                               submitAction: Call = DateOfSale.submitWithDateCheck(),
                               dateToCompare: Option[String] = None)
