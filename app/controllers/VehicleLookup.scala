package controllers

import play.api.mvc.{Action, Controller}
import models.VehicleLookupFormModel
import com.google.inject.Inject
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import utils.helpers.Config
import models.VehicleLookupViewModel


class VehicleLookup @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                config: Config) extends Controller {

  private[controllers] val form = Form(
    VehicleLookupFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.vehicle_lookup(VehicleLookupViewModel(form.fill())))
  }

  def submit = Action { implicit request =>
    Ok("success")
  }

}