package helpers

import org.scalatest.Assertions.fail
import play.api.http.HeaderNames.SET_COOKIE
import play.api.mvc.{Cookie, Cookies, Result}

// TODO: this should be a common test scope class. vehicles-online also has a version
object CookieHelper {
  def fetchCookiesFromHeaders(result: Result): Seq[Cookie] =
    result.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)

  def verifyCookieHasBeenDiscarded(cookieName: String, cookies: Seq[Cookie]) = {
    // A discarded cookie is identified by a negative maxAge
    val cookie = cookies.find(_.name == cookieName)
    cookie.get.maxAge match {
      case Some(maxAge) if maxAge < 0 => // Success
      case Some(maxAge) => fail(s"maxAge should be negative but was $maxAge")
      case _ => fail("should be some maxAge")
    }
  }
}
