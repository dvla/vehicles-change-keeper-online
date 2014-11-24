package controllers

import com.google.inject.Inject
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import models.{AllCacheKeys, VrmLockedViewModel}
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.model.BruteForcePreventionModel
import utils.helpers.Config

class VrmLocked @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller {

  def present = Action { implicit request =>
    request.cookies.getModel[BruteForcePreventionModel] match {
      case Some(viewModel) =>
        Logger.debug(s"VrmLocked - Displaying the vrm locked error page")
        Ok(
          views.html.changekeeper.vrm_locked(
            VrmLockedViewModel(
              viewModel.dateTimeISOChronology,
              DateTime.parse(viewModel.dateTimeISOChronology).getMillis
            )
          )
        )
      case None =>
        Logger.debug("VrmLocked - Can't find cookie for BruteForcePreventionViewModel")
        Redirect(routes.VehicleLookup.present())
    }
  }

  def buyAnotherVehicle = Action { implicit request =>
    Redirect(routes.VehicleLookup.present())
      .discardingCookies(Set(VehicleLookupFormModelCacheKey))
  }

  def exit = Action { implicit request =>
    Redirect(routes.BeforeYouStart.present()).discardingCookies(AllCacheKeys)
  }
}