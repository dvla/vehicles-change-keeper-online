package controllers

import com.google.inject.Inject
import models.AllCacheKeys
import models.ChangeKeeperCompletionViewModel
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmResponseModel
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.model.NewKeeperDetailsViewModel
import common.model.VehicleAndKeeperDetailsModel
import utils.helpers.Config

class ChangeKeeperSuccess @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                       config: Config) extends Controller {

  private final val MissingCookiesSuccess = "Missing cookies in cache. Change of keeper was successful, " +
    "however cannot display success page. Redirecting to BeforeYouStart"

  def present = Action { implicit request =>
    (request.cookies.getModel[VehicleAndKeeperDetailsModel],
      request.cookies.getModel[NewKeeperDetailsViewModel],
      request.cookies.getModel[CompleteAndConfirmFormModel],
      request.cookies.getModel[CompleteAndConfirmResponseModel]
      ) match {
      case (Some(vehicleAndKeeperDetailsModel), Some(newKeeperDetailsModel), Some(completeAndConfirmModel), Some(responseModel)) =>
        Ok(views.html.changekeeper.change_keeper_success(ChangeKeeperCompletionViewModel(
          vehicleAndKeeperDetailsModel, newKeeperDetailsModel, completeAndConfirmModel, responseModel
        )))
      case _ => redirectToStart(MissingCookiesSuccess)
    }
  }

  def finish = Action { implicit request =>
    Redirect(routes.BeforeYouStart.present())
      .discardingCookies(AllCacheKeys)
  }

  private def redirectToStart(message: String) = {
    Logger.warn(message)
    Redirect(routes.BeforeYouStart.present())
  }
}