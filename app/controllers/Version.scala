package controllers

import play.api.mvc.{Action, Controller}
import scala.io.Source.fromInputStream

class Version extends Controller {

  def version = Action { implicit request =>
    Ok(fromInputStream(getClass.getResourceAsStream("/build-details.txt")).mkString)
  }

}
