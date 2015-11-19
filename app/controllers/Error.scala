package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.LogFormats.DVLALogger
import common.utils.helpers.CookieHelper

import utils.helpers.Config

final class Error @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                              config: Config) extends Controller with DVLALogger {

  def present(exceptionDigest: String) = Action { implicit request =>
    logMessage(request.cookies.trackingId(), Error, "Displaying generic error page")
    Ok(views.html.changekeeper.error(exceptionDigest))
  }

  def submit(exceptionDigest: String) = Action { implicit request =>
    logMessage(
      request.cookies.trackingId(),
      Error,
      "Submit called - now removing full set of cookies and redirecting to Start page"
    )
    CookieHelper.discardAllCookies(routes.BeforeYouStart.present)
  }
}