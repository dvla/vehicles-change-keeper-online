package controllers

import com.google.inject.Inject
import models.{BusinessKeeperDetailsViewModel, BusinessKeeperDetailsFormModel}
import play.api.data.{FormError, Form}
import play.api.mvc.{Action, Controller}
import play.api.Logger
import utils.helpers.Config
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.{VehicleAndKeeperDetailsModel, VehicleDetailsModel}
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.views.helpers.FormExtensions.formBinding
import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey

class BusinessKeeperDetails @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                        config: Config) extends Controller {

  private[controllers] val form = Form(
    BusinessKeeperDetailsFormModel.Form.Mapping
  )

  private final val CookieErrorMessage = "Did not find VehicleDetailsModel cookie. Now redirecting to SetUpTradeDetails."

  def present = Action { implicit request =>
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        Ok(views.html.changekeeper.business_keeper_details(
          BusinessKeeperDetailsViewModel(form.fill())
        ))
      case _ => redirectToVehicleLookup(CookieErrorMessage)
    }
  }

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
          case Some(vehicleDetails) =>
            BadRequest(views.html.changekeeper.business_keeper_details(
              BusinessKeeperDetailsViewModel(formWithReplacedErrors(invalidForm))
            ))
          case None => redirectToVehicleLookup(CookieErrorMessage)
        },
      validForm => Redirect(routes.NewKeeperChooseYourAddress.present()).withCookie(validForm)
        .discardingCookie(NewKeeperChooseYourAddressCacheKey)
    )
  }

  private def formWithReplacedErrors(form: Form[BusinessKeeperDetailsFormModel]) = {
    form.replaceError(
      BusinessKeeperDetailsFormModel.Form.BusinessNameId,
      FormError(key = BusinessKeeperDetailsFormModel.Form.BusinessNameId,message = "error.validBusinessKeeperName")
    ).replaceError(
        BusinessKeeperDetailsFormModel.Form.EmailId,
        FormError(key = BusinessKeeperDetailsFormModel.Form.EmailId,message = "error.email")
      ).replaceError(
        BusinessKeeperDetailsFormModel.Form.PostcodeId,
        FormError(key = BusinessKeeperDetailsFormModel.Form.PostcodeId,message = "error.restricted.validPostcode")
      ).distinctErrors
  }

  private def redirectToVehicleLookup(message:String) = {
    Logger.warn(message)
    Redirect(routes.VehicleLookup.present())
  }
}