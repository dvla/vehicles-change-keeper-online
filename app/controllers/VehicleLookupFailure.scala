package controllers

import com.google.inject.Inject
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import play.Logger
import play.api.mvc.{AnyContent, Controller, Action, Request, DiscardingCookie}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichResult
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.{BruteForcePreventionModel, TraderDetailsModel}
import utils.helpers.Config

final class VehicleLookupFailure @Inject()()
                                 (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller {

  def present = Action { implicit request =>
    ( request.cookies.getModel[BruteForcePreventionModel],
      request.cookies.getModel[VehicleLookupFormModel],
      request.cookies.getString(VehicleLookupResponseCodeCacheKey)) match {
      case (Some(bruteForcePreventionResponse),
            Some(vehicleLookUpFormModelDetails),
            Some(vehicleLookupResponseCode)) =>
        val responseMessage = vehicleLookupResponseCode.split("-").map(_.trim)
        displayVehicleLookupFailure(
          vehicleLookUpFormModelDetails,
          bruteForcePreventionResponse,
          responseMessage.last
        )
      case _ => Redirect(routes.BeforeYouStart.present())
    }
  }

  def submit = Action { implicit request =>
    request.cookies.getModel[VehicleLookupFormModel]
      .fold(Redirect(routes.BeforeYouStart.present())) { vehicleLookUpFormModelDetails =>
        Logger.debug("Found dealer and vehicle details")
        Redirect(routes.VehicleLookup.present())
      }
  }

  private def displayVehicleLookupFailure(vehicleLookUpFormModelDetails: VehicleLookupFormModel,
                                          bruteForcePreventionViewModel: BruteForcePreventionModel,
                                          vehicleLookupResponseCode: String)(implicit request: Request[AnyContent]) = {
    Ok(views.html.changekeeper.vehicle_lookup_failure(
      data = vehicleLookUpFormModelDetails,
      responseCodeVehicleLookupMSErrorMessage = vehicleLookupResponseCode
    )).discardingCookies(DiscardingCookie(name = VehicleLookupResponseCodeCacheKey))
  }
}
