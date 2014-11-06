package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.Logger
import play.api.data.{FormError, Form}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.model.VehicleDetailsModel
import common.clientsidesession.CookieImplicits.RichCookies
import common.views.helpers.FormExtensions.formBinding
import common.clientsidesession.CookieImplicits.RichResult
import utils.helpers.Config

class PrivateKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.private_keeper_details())
  }

  def submit = Action { implicit request =>
    Ok("success")
  }
}