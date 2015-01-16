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

object TestGlobal extends GlobalLike with TestComposition

trait ChangeKeeperGlobalCreator extends GlobalCreator {
  override def global: GlobalSettings = TestGlobal
}
