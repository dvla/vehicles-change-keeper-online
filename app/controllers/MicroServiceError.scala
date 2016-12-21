package controllers

import com.google.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.LogFormats.DVLALogger
import utils.helpers.Config
import uk.gov.dvla.vehicles.presentation.common.mappings.Time.fromMinutes

class MicroServiceError @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller with DVLALogger {

  private final val DefaultRedirectUrl = controllers.routes.VehicleLookup.present().url
  protected val tryAgainTarget = controllers.routes.MicroServiceError.back()
  protected val exitTarget = controllers.routes.BeforeYouStart.present()

  def present = Action { implicit request =>
    val trackingId = request.cookies.trackingId()
    logMessage(trackingId, Debug, "Presenting micro service error page")

    val referer = request.headers.get(REFERER) match {
      case Some(ref) => new java.net.URI(ref).getPath()
      case None => DefaultRedirectUrl
    }

    logMessage(request.cookies.trackingId(), Debug, s"Referer $referer")
    logMessage(request.cookies.trackingId(), Debug, s"Try again target $tryAgainTarget")

    val unavailable = ServiceUnavailable(
      views.html.changekeeper.micro_service_error(
        fromMinutes(config.openingTimeMinOfDay),
        fromMinutes(config.closingTimeMinOfDay),
        tryAgainTarget,
        exitTarget
      )
    )

    // Doesn't make sense to store this page as its own referer
    if (request.path != referer)
      // Save the previous page URL (from the referrer header) into a cookie.
      unavailable.withCookie(MicroServiceError.MicroServiceErrorRefererCacheKey, referer)
    else
      unavailable
  }

  def back = Action { implicit request =>
    val referrer = request.cookies
      .getString(MicroServiceError.MicroServiceErrorRefererCacheKey)
      .getOrElse(DefaultRedirectUrl)
    Redirect(referrer).discardingCookie(MicroServiceError.MicroServiceErrorRefererCacheKey)
  }
}

object MicroServiceError {
  final val MicroServiceErrorRefererCacheKey = s"${CookiePrefix}msError"
}
