package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import utils.helpers.Config

/* Controller for redirecting people to the start page if the enter the application using the url "/" */
class Application @Inject()(implicit config: Config) extends Controller {
  private final val startUrl: String = config.startUrl

  def index = Action {
    play.api.Logger.debug(s"Redirecting to $startUrl...")
    Redirect(startUrl)
  }
}