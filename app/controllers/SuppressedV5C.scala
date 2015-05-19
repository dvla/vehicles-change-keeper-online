package controllers

import com.google.inject.Inject
import models.AllCacheKeys
import play.api.mvc.{Action, Controller}
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichResult
import common.model.VehicleAndKeeperDetailsModel
import common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.LogFormats._
import utils.helpers.Config

class SuppressedV5C @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                         config: Config) extends Controller {

  def present = Action { implicit request =>
        Ok(views.html.changekeeper.suppressedV5C())
  }

  def finish = Action { implicit request =>
    Logger.debug(logMessage(s"Redirecting to ${routes.BeforeYouStart.present()}", request.cookies.trackingId()))
    Redirect(routes.BeforeYouStart.present()).
      discardingCookies(AllCacheKeys)
  }
}