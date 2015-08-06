package utils.helpers

import controllers.routes
import play.api.mvc.Results.Redirect
import play.api.mvc.{DiscardingCookie, RequestHeader, Result}
import models.SeenCookieMessageCacheKey
import scala.concurrent.Future

object CookieHelper {
  def discardAllCookies(implicit request: RequestHeader): Result = {
    val discardingCookiesKeys = request.cookies.map(_.name).filter(_ != SeenCookieMessageCacheKey)
    val discardingCookies = discardingCookiesKeys.map(DiscardingCookie(_)).toSeq
    Redirect(routes.BeforeYouStart.present())
      .discardingCookies(discardingCookies: _*)
  }
}