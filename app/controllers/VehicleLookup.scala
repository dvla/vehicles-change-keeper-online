package controllers

import play.api.mvc.Controller
import models.VehicleLookupFormModel.Form.{DocumentReferenceNumberId, VehicleRegistrationNumberId, VehicleSoldToId}
import com.google.inject.Inject
import play.api.data.{Form, FormError}
import play.api.mvc.Action
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.views.helpers.FormExtensions.formBinding
import utils.helpers.Config
import models.{VehicleLookupFormModel, VehicleLookupViewModel}


class VehicleLookup @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                config: Config) extends Controller {

  private[controllers] val form = Form(
    VehicleLookupFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.vehicle_lookup(
      VehicleLookupViewModel(form.fill()))
    )
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => BadRequest(views.html.changekeeper.vehicle_lookup(
        VehicleLookupViewModel(formWithReplacedErrors(invalidForm)))
      ),
      validForm => Ok("success")
    )
  }

  private def formWithReplacedErrors(form: Form[VehicleLookupFormModel]) = {
    form.replaceError(
      VehicleRegistrationNumberId, FormError(
        key = VehicleRegistrationNumberId,
        message = "error.restricted.validVrnOnly",
        args = Seq.empty)
    ).replaceError(
        DocumentReferenceNumberId,FormError(
          key = DocumentReferenceNumberId,
          message = "error.validDocumentReferenceNumber",
          args = Seq.empty)
      ).replaceError(
        VehicleSoldToId,FormError(
      key = VehicleSoldToId,
      message = "error.validBougtByType",
      args = Seq.empty)
      ).distinctErrors
  }

}