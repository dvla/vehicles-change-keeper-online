package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.mvc.{Result, Request}
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.controllers.PrivateKeeperDetailsBase
import common.model.{PrivateKeeperDetailsFormModel, VehicleAndKeeperDetailsModel}
import common.services.DateService
import utils.helpers.Config

class PrivateKeeperDetails @Inject()()(implicit protected override val clientSideSessionFactory: ClientSideSessionFactory,
                                       dateService: DateService,
                                       config: Config) extends PrivateKeeperDetailsBase {

  protected override def presentResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                                      (implicit request: Request[_]): Result =
    Ok(views.html.changekeeper.private_keeper_details(model, form))

  protected override def missingVehicleDetails(implicit request: Request[_]): Result =
    Redirect(routes.VehicleLookup.present())

  protected override def invalidFormResult(model: VehicleAndKeeperDetailsModel, form: Form[PrivateKeeperDetailsFormModel])
                                          (implicit request: Request[_]): Result =
    BadRequest(views.html.changekeeper.private_keeper_details(model, form))

  protected override def success(implicit request: Request[_]): Result =
    Redirect(routes.NewKeeperChooseYourAddress.present())
}

/*package controllers

import com.google.inject.Inject
import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
import models.PrivateKeeperDetailsFormModel
import models.PrivateKeeperDetailsFormModel.Form.{LastNameId, PostcodeId, DriverNumberId, EmailId}
import play.api.data.{FormError, Form}
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import common.model.VehicleAndKeeperDetailsModel
import common.views.helpers.FormExtensions.formBinding
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import utils.helpers.Config

class PrivateKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       dateService: DateService,
                                       config: Config) extends Controller {

  private final val NoValidCookiePresent = "Appropriate cookie not found to present PrivateKeeperDetails, redirecting..."
  private final val NoValidCookieSubmit = "Appropriate cookie not found to submit PrivateKeeperDetails, redirecting..."

  private[controllers] val form = Form(
    PrivateKeeperDetailsFormModel.Form.detailMapping
  )

  def present = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) => Ok(views.html.changekeeper.private_keeper_details(vehicleDetails, form.fill()))
      case _ => redirectToVehicleLookup(NoValidCookiePresent)
    }
  }

  def submit = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        form.bindFromRequest.fold(
          invalidForm => BadRequest(views.html.changekeeper.private_keeper_details(
            vehicleDetails,
            formWithReplacedErrors(invalidForm))),
          validForm => Redirect(routes.NewKeeperChooseYourAddress.present()).withCookie(validForm)
                        .discardingCookie(NewKeeperChooseYourAddressCacheKey))
      case _ =>
        Logger.debug("form ->")
        redirectToVehicleLookup(NoValidCookieSubmit)
    }
  }

  private def formWithReplacedErrors(form: Form[PrivateKeeperDetailsFormModel]) = {
    form.replaceError(
       LastNameId, FormError(key = LastNameId,message = "error.validLastName", args = Seq.empty)
     ).replaceError(
       DriverNumberId, FormError(key = DriverNumberId, message = "error.validDriverNumber", args = Seq.empty)
     ).replaceError(
       EmailId, FormError(key = EmailId, message = "error.email", args = Seq.empty)
     ).replaceError(
       PostcodeId, FormError(key = PostcodeId, message = "error.restricted.validPostcode", args = Seq.empty)
     ).distinctErrors
  }

  private def redirectToVehicleLookup(message: String) = {
    Logger.debug(message)
    Redirect(routes.VehicleLookup.present())
  }
}
*/