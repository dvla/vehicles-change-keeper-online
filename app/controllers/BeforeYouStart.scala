package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichResult
import utils.helpers.Config
import models.AllCacheKeys

class BeforeYouStart @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.before_you_start()).
      withNewSession.
      discardingCookies(AllCacheKeys)
  }

  def submit = Action { implicit request =>
    Redirect(routes.VehicleLookup.present())
  }
}