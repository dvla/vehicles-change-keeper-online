package utils.helpers

import controllers.routes
import models.SeenCookieMessageCacheKey
import play.api.mvc.DiscardingCookie
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results.Redirect

object CookieHelper {
  def discardAllCookies(implicit request: RequestHeader): Result = {
    val discardingCookiesKeys = request.cookies.map(_.name).filter(_ != SeenCookieMessageCacheKey)
    val discardingCookies = discardingCookiesKeys.map(DiscardingCookie(_)).toSeq
    Redirect(routes.BeforeYouStart.present())
      .discardingCookies(discardingCookies: _*)
  }
}