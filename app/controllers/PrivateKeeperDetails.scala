package controllers

import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.Logger
import models.PrivateKeeperDetailsFormModel
import play.api.data.{FormError, Form}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import utils.helpers.Config

class PrivateKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  private final val NoValidCookiePresent = "Appropriate cookie not found to access PrivateKeeperDetails, redirecting..."

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
    Ok("success")
  }

  private def redirectToVehicleLookup(message: String) = {
    Logger.debug(message)
    Redirect(routes.VehicleLookup.present())
  }
}