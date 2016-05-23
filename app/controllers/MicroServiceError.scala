package controllers

import com.google.inject.Inject
import controllers.routes.BeforeYouStart
import java.util.Locale
import models.K2KCacheKeyPrefix.CookiePrefix
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.LogFormats.DVLALogger
import utils.helpers.Config

class MicroServiceError @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller with DVLALogger {

  private final val DefaultRedirectUrl = BeforeYouStart.present().url
  protected val tryAgainTarget = controllers.routes.MicroServiceError.back()
  protected val exitTarget = controllers.routes.BeforeYouStart.present()

  def present = Action { implicit request =>
    val trackingId = request.cookies.trackingId()
    logMessage(trackingId, Info, "Presenting micro service error view")

    val referer = request.headers.get(REFERER).getOrElse(DefaultRedirectUrl)
    logMessage(request.cookies.trackingId(), Debug, s"Referer $referer")

    ServiceUnavailable(
      views.html.changekeeper.micro_service_error(
        h(config.openingTimeMinOfDay * MillisInMinute),
        h(config.closingTimeMinOfDay * MillisInMinute),
        tryAgainTarget,
        exitTarget
      )
    )
    // Save the previous page URL (from the referer header) into a cookie.
    .withCookie(MicroServiceError.MicroServiceErrorRefererCacheKey, referer)
  }

  def back = Action { implicit request =>
    val referrer: String = request.cookies.getString(MicroServiceError.MicroServiceErrorRefererCacheKey)
                                          .getOrElse(DefaultRedirectUrl)
    logMessage(request.cookies.trackingId(), Debug, s"Micro service error page referrer $referrer")
    Redirect(referrer).discardingCookie(MicroServiceError.MicroServiceErrorRefererCacheKey)
  }

  private final val MillisInMinute = 60 * 1000L

  private def h(hourMillis: Long) =
    DateTimeFormat.forPattern("HH:mm").withLocale(Locale.UK)
      .print(new DateTime(hourMillis, DateTimeZone.forID("UTC"))).toLowerCase // Must use UTC as we only want to format the hour
}

object MicroServiceError {
  final val MicroServiceErrorRefererCacheKey = s"${CookiePrefix}msError"
}
