package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.controllers.VehicleLookupFailureBase
import utils.helpers.Config

final class VehicleLookupFailure @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                             config: Config) extends VehicleLookupFailureBase[VehicleLookupFormModel] {

  override def presentResult(model: VehicleLookupFormModel)
                            (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Info, "Presenting vehicle lookup failure view")
    Ok(views.html.changekeeper.vehicle_lookup_failure(
      data = model )
    )
  }

  override def missingPresentCookieDataResult()(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.BeforeYouStart.present()}")
    Redirect(routes.BeforeYouStart.present())
  }

  override def submitResult()(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.VehicleLookup.present()}")
    Redirect(routes.VehicleLookup.present())
  }

  override def missingSubmitCookieDataResult()(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.BeforeYouStart.present()}")
    Redirect(routes.BeforeYouStart.present())
  }
}
