package controllers

import com.google.inject.Inject
import controllers.routes.BeforeYouStart
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import uk.gov.dvla.vehicles.presentation.common.LogFormats._
import utils.helpers.Config

class MicroServiceError @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                  config: Config) extends Controller {
  //  private final val DefaultRedirectUrl = VehicleLookup.present().url
  private final val DefaultRedirectUrl = BeforeYouStart.present().url

  def present = Action { implicit request =>
    Logger.debug(logMessage(s"Present serviceUnavailable page", request.cookies.trackingId()))
    ServiceUnavailable(views.html.changekeeper.micro_service_error())
  }

  def back = Action { implicit request =>
    val referer: String = request.cookies.getString(MicroServiceError.MicroServiceErrorRefererCacheKey).getOrElse(DefaultRedirectUrl)
    Logger.debug(logMessage(s"Microservice error page referer ${referer}", request.cookies.trackingId()))
    Redirect(referer).discardingCookie(MicroServiceError.MicroServiceErrorRefererCacheKey)
  }
}

object MicroServiceError {
  final val MicroServiceErrorRefererCacheKey = s"${CookiePrefix}msError"
}
