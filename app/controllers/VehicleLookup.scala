package controllers

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config
import play.api.mvc.{Action, Controller}


class VehicleLookup @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                config: Config) extends Controller {

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.vehicle_lookup())
  }

}
