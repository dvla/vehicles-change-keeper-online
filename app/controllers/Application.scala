package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import utils.helpers.Config

/* Controller for redirecting people to the start page if the enter the application using the url "/" */
class Application @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                            config: Config) extends Controller with DVLALogger {
  private final val startUrl: String = config.startUrl

  def index = Action { implicit request =>
    val msg = s"User has entered on application root, now redirecting to $startUrl..."
    logMessage(request.cookies.trackingId(), Debug, msg)
    Redirect(startUrl)
  }
}