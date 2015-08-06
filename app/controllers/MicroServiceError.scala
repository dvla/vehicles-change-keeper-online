package controllers

import com.google.inject.Inject
import controllers.routes.BeforeYouStart
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import utils.helpers.Config

class MicroServiceError @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller with DVLALogger {
  //  private final val DefaultRedirectUrl = VehicleLookup.present().url
  private final val DefaultRedirectUrl = BeforeYouStart.present().url

  def present = Action { implicit request =>
    logMessage(request.cookies.trackingId(), Debug, s"Present serviceUnavailable page")
    ServiceUnavailable(views.html.changekeeper.micro_service_error())
  }

  def back = Action { implicit request =>
    val referrer: String = request.cookies.getString(MicroServiceError.MicroServiceErrorRefererCacheKey).getOrElse(DefaultRedirectUrl)
    logMessage(request.cookies.trackingId(), Debug, s"Microservice error page referrer $referrer")
    Redirect(referrer).discardingCookie(MicroServiceError.MicroServiceErrorRefererCacheKey)
  }
}

object MicroServiceError {
  final val MicroServiceErrorRefererCacheKey = s"${CookiePrefix}msError"
}
