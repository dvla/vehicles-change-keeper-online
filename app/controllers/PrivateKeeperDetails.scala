package controllers

import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.Logger
import models.PrivateKeeperDetailsFormModel
import models.PrivateKeeperDetailsFormModel.Form.{LastNameId, PostcodeId, DriverNumberId, EmailId}
import play.api.data.{FormError, Form}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.views.helpers.FormExtensions.formBinding
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import utils.helpers.Config

class PrivateKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  private final val NoValidCookiePresent = "Appropriate cookie not found to present PrivateKeeperDetails, redirecting..."
  private final val NoValidCookieSubmit = "Appropriate cookie not found to submit PrivateKeeperDetails, redirecting..."

  private[controllers] val form = Form(
    PrivateKeeperDetailsFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) => Ok(views.html.changekeeper.private_keeper_details(form.fill()))
      case _ => redirectToVehicleLookup(NoValidCookiePresent)
    }
  }

  def submit = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        form.bindFromRequest.fold(
          invalidForm => BadRequest(views.html.changekeeper.private_keeper_details(formWithReplacedErrors(invalidForm))),
          validForm => Ok("success"))
      case _ => redirectToVehicleLookup(NoValidCookieSubmit)
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