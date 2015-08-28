package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.model.BusinessKeeperDetailsViewModel
import common.controllers.BusinessKeeperDetailsBase
import utils.helpers.Config

class BusinessKeeperDetails @Inject()()(implicit protected override val clientSideSessionFactory: ClientSideSessionFactory,
                                        val config: Config) extends BusinessKeeperDetailsBase {

  protected override def presentResult(model: BusinessKeeperDetailsViewModel)(implicit request: Request[_]): Result =
    Ok(views.html.changekeeper.business_keeper_details(model))

  protected def invalidFormResult(model:BusinessKeeperDetailsViewModel)(implicit request: Request[_]): Result =
    BadRequest(views.html.changekeeper.business_keeper_details(model))

  protected def missingVehicleDetails(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(),
      Warn,
      s"Missing vehicle details, now redirecting to ${routes.VehicleLookup.present()}"
    )
    Redirect(routes.VehicleLookup.present())
  }

  protected def success(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.NewKeeperChooseYourAddress.present()}")
    Redirect(routes.NewKeeperChooseYourAddress.present())
  }
}
