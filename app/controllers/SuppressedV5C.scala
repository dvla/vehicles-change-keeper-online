package controllers

import com.google.inject.Inject
import models.AllCacheKeys
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.clientsidesession.CookieImplicits.RichResult
import common.LogFormats.DVLALogger
import utils.helpers.Config

class SuppressedV5C @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                         config: Config) extends Controller with DVLALogger{

  def present = Action { implicit request =>
        Ok(views.html.changekeeper.suppressedV5C())
  }

  def finish = Action { implicit request =>
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.BeforeYouStart.present()}")
    Redirect(routes.BeforeYouStart.present()).
      discardingCookies(AllCacheKeys)
  }
}