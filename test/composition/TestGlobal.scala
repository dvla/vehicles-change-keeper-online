package composition

import play.api._
import play.api.i18n.Lang
import play.api.mvc.Results._
import play.api.mvc.{Result, RequestHeader}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.GlobalCreator
import utils.helpers.Config
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

import play.api.Play.current

object TestGlobal extends GlobalLike with TestComposition {

  override def onStart(app: Application) {
    Logger.info("vehicles-change-keeper Started") // used for operations, do not remove
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("vehicles-change-keeper Stopped") // used for operations, do not remove
  }

  // 404 - page not found error http://alvinalexander.com/scala/handling-scala-play-framework-2-404-500-errors
  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future.successful {
      val playLangCookie = request.cookies.get(Play.langCookieName)
      val value: String = playLangCookie match {
        case Some(cookie) => cookie.value
        case None => "en"
      }
      implicit val lang: Lang = Lang(value)
      implicit val config = TestConfig
      Logger.warn(s"Broken link returning http code 404. uri: ${request.uri}")
      NotFound(views.html.errors.onHandlerNotFound(request))
    }
  }

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] =
    errorStrategy(request, ex)
}

trait ChangeKeeperGlobalCreator extends GlobalCreator {
  override def global: GlobalSettings = TestGlobal
}
