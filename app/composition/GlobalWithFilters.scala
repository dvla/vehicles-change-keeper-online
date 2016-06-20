package composition

import play.api.mvc.RequestHeader
import uk.gov.dvla.vehicles.presentation.common.CommonGlobalSettings
import utils.helpers.Config

trait GlobalWithFilters extends CommonGlobalSettings with WithFilters {

  val serviceName = "vehicles-change-keeper-online"

  def viewNotFound(request: RequestHeader) = {
    implicit val config = injector.getInstance(classOf[Config])
    views.html.errors.onHandlerNotFound(request)
  }
}
