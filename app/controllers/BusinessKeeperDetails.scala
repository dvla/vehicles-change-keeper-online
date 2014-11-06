package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import play.api.data.FormError
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichResult
import utils.helpers.Config

class BusinessKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.business_keeper_details())
  }

  def submit = Action { implicit request =>
    Ok("success")
  }
}