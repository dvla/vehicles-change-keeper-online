package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.LogFormats.DVLALogger
import utils.helpers.Config

class UprnNotFound @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                     config: Config)  extends Controller with DVLALogger {

  def present = Action { implicit request =>
    logMessage(request.cookies.trackingId(), Warn, "Uprn not found")
    Ok(views.html.common.uprn_not_found())
  }
}