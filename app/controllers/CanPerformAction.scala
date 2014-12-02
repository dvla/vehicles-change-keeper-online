package controllers

import models.CompleteAndConfirmFormModel._
import play.api.Logger
import play.api.mvc.Request
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies

/**
 * !Move this to a common repository!
 *
 * This trait should be mixed with a controller that requires the presence of
 * <code>AllowGoingToCompleteAndConfirmPageCacheKey</code> to allow for the completion of the request.
 *
 * you need to call the canPerform method on each controller method. Check the examples bellow.
 *
 * Created by gerasimosarvanitis on 02/12/2014.
 */
trait CanPerformAction {

  /**
   * Checks the presence of <code>AllowGoingToCompleteAndConfirmPageCacheKey</code> to allow the completion of
   * the request, otherwise calls redirect function.
   *
   * Example:
   * def present = Action { implicit request =>
   *  canPerform {
   *    Ok("success")
   *  }(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway))
   * }
   *
   * or
   * def present = Action.async { implicit request =>
   *  canPerform {
   *    ...
   *  }(Future.successful(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway)))
   * }
   *
   * @param action the action body
   * @param redirect the Result function to be called if the cookie is not present
   * @param request implicit request
   * @tparam T for the purposes of the application this should be Either a Result of Future[Result]
   * @return T by either calling the action of the redirect
   */
   protected def canPerform[T](action: => T)(redirect: => T)
                              (implicit request: Request[_], clientSideSessionFactory: ClientSideSessionFactory)= {
    request.cookies.getString(AllowGoingToCompleteAndConfirmPageCacheKey).fold {
      Logger.warn(s"Could not find AllowGoingToCompleteAndConfirmPageCacheKey in the request. " +
        s"Redirect to starting page discarding cookies")
      redirect
    }(c => action)
  }
}
