package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupFailureBase
import utils.helpers.Config

final class VehicleLookupFailure @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                             config: Config) extends VehicleLookupFailureBase[VehicleLookupFormModel] {

  override val vehicleLookupResponseCodeCacheKey: String = VehicleLookupResponseCodeCacheKey

  override def presentResult(model: VehicleLookupFormModel, responseCode: String)(implicit request: Request[_]): Result =
    Ok(views.html.changekeeper.vehicle_lookup_failure(
      data = model,
      responseCodeVehicleLookupMSErrorMessage = responseCode)
    )

  override def missingPresentCookieDataResult()(implicit request: Request[_]): Result =
    Redirect(routes.BeforeYouStart.present())

  override def submitResult()(implicit request: Request[_]): Result =
    Redirect(routes.VehicleLookup.present())

  override def missingSubmitCookieDataResult()(implicit request: Request[_]): Result =
    Redirect(routes.BeforeYouStart.present())
}
