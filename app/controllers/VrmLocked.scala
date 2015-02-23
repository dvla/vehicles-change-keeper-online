package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import models.{AllCacheKeys, VrmLockedViewModel}
import org.joda.time.DateTime
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichResult
import common.controllers.VrmLockedBase
import common.model.BruteForcePreventionModel
import utils.helpers.Config

class VrmLocked @Inject()()(implicit protected override val clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends VrmLockedBase {

  protected override def presentResult(model: BruteForcePreventionModel)
                                      (implicit request: Request[_]): Result =
    Ok(views.html.changekeeper.vrm_locked(
      VrmLockedViewModel(model.dateTimeISOChronology, DateTime.parse(model.dateTimeISOChronology).getMillis)
    ))

  protected override def missingBruteForcePreventionCookie(implicit request: Request[_]): Result =
    Redirect(routes.VehicleLookup.present())

  protected override def tryAnotherResult(implicit request: Request[_]): Result =
    Redirect(routes.VehicleLookup.present())
      .discardingCookies(Set(VehicleLookupFormModelCacheKey))

  protected override def exitResult(implicit request: Request[_]): Result =
    Redirect(routes.BeforeYouStart.present()).discardingCookies(AllCacheKeys)
}
