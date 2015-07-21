package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.LogFormats.logMessage
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies

import utils.helpers.{CookieHelper, Config}

final class Error @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                              config: Config) extends Controller {

  def present(exceptionDigest: String) = Action { implicit request =>
    Logger.debug(logMessage("Error - Displaying generic error page", request.cookies.trackingId()))
    Ok(views.html.changekeeper.error(exceptionDigest))
  }

  def submit(exceptionDigest: String) = Action { implicit request =>
    Logger.debug(logMessage("Error submit called - now removing full set of cookies and redirecting to Start page",
      request.cookies.trackingId()))
    CookieHelper.discardAllCookies
  }
}