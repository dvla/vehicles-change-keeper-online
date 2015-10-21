package controllers

import com.google.inject.Inject
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.data.Form
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichCookies, RichResult}
import common.controllers.NewKeeperEnterAddressManuallyBase
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.model.NewKeeperEnterAddressManuallyViewModel
import common.model.VehicleAndKeeperDetailsModel
import utils.helpers.Config
import views.html.changekeeper.new_keeper_enter_address_manually

class NewKeeperEnterAddressManually @Inject()()
                                             (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                              config: Config) extends NewKeeperEnterAddressManuallyBase {

  protected override def presentResult(model: VehicleAndKeeperDetailsModel,
                                       form: Form[NewKeeperEnterAddressManuallyFormModel])
                                      (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Info, "Presenting new keeper enter address manually view")
    Ok(new_keeper_enter_address_manually(NewKeeperEnterAddressManuallyViewModel(form, model)))
  }

  protected override def missingVehicleDetails(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.VehicleLookup.present()}")
    Redirect(routes.VehicleLookup.present())
  }

  protected override def invalidFormResult(model: VehicleAndKeeperDetailsModel,
                                           form: Form[NewKeeperEnterAddressManuallyFormModel])
                                          (implicit request: Request[_]): Result =
    BadRequest(new_keeper_enter_address_manually(
      NewKeeperEnterAddressManuallyViewModel(formWithReplacedErrors(form), model)))

  protected override def success(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.DateOfSale.present()}")
    Redirect(routes.DateOfSale.present()).
      withCookie(AllowGoingToCompleteAndConfirmPageCacheKey, "true")
  }
}
