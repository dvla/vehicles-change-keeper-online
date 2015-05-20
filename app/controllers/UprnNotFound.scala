package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.LogFormats.logMessage
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import utils.helpers.Config

class UprnNotFound @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                     config: Config)  extends Controller {

  def present = Action { implicit request =>
    Logger.warn(logMessage(s"Uprn not found", request.cookies.trackingId()))
    Ok(views.html.common.uprn_not_found())
  }
}