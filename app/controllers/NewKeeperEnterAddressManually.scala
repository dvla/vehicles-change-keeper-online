package controllers

import com.google.inject.Inject
import models.NewKeeperDetailsViewModel.createNewKeeper
import models.{NewKeeperEnterAddressManuallyViewModel, NewKeeperEnterAddressManuallyFormModel, BusinessKeeperDetailsFormModel, PrivateKeeperDetailsFormModel}
import play.api.Logger
import play.api.data.{Form, FormError}
import play.api.mvc.{Action, AnyContent, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult, RichForm}
import common.model.{VehicleAndKeeperDetailsModel, AddressModel}
import common.views.helpers.FormExtensions.formBinding
import utils.helpers.Config
import views.html.changekeeper.new_keeper_enter_address_manually
import models.CompleteAndConfirmFormModel._
import scala.Some
import models.NewKeeperEnterAddressManuallyViewModel
import play.api.mvc.Result

class NewKeeperEnterAddressManually @Inject()()
                                          (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                           config: Config) extends Controller {

  private[controllers] val form = Form(
    NewKeeperEnterAddressManuallyFormModel.Form.Mapping
  )

  private final val KeeperDetailsNotInCacheMessage = "Failed to find keeper details in cache. " +
    "Now redirecting to vehicle lookup."
  private final val PrivateAndBusinessKeeperDetailsBothInCacheMessage = "Both private and business keeper details " +
    "found in cache. This is an error condition. Now redirecting to vehicle lookup."
  private final val VehicleDetailsNotInCacheMessage = "Failed to find vehicle details in cache. " +
    "Now redirecting to vehicle lookup"

  def present = Action { implicit request => switch(
    privateKeeperDetails => openView(privateKeeperDetails.postcode),
    businessKeeperDetails => openView(businessKeeperDetails.postcode),
    message => error(message)
  )}

  def submit = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => switch(
        privateKeeperDetails =>
          handleInvalidForm(invalidForm, privateKeeperDetails.postcode),
        businessKeeperDetails =>
          handleInvalidForm(invalidForm, businessKeeperDetails.postcode),
        message => error(message)
      ),
      validForm => switch(
        privateKeeperDetails =>
          handleValidForm(validForm, privateKeeperDetails.postcode),
        businessKeeperDetails =>
          handleValidForm(validForm, businessKeeperDetails.postcode),
        message => error(message)
      )
    )
  }

  private def switch[R](onPrivate: PrivateKeeperDetailsFormModel => R,
                        onBusiness: BusinessKeeperDetailsFormModel => R,
                        onError: String => R)
                       (implicit request: Request[AnyContent]): R = {
    val privateKeeperDetailsOpt = request.cookies.getModel[PrivateKeeperDetailsFormModel]
    val businessKeeperDetailsOpt = request.cookies.getModel[BusinessKeeperDetailsFormModel]
    (privateKeeperDetailsOpt, businessKeeperDetailsOpt) match {
      case (Some(privateKeeperDetails), Some(businessKeeperDetails)) => onError(PrivateAndBusinessKeeperDetailsBothInCacheMessage)
      case (Some(privateKeeperDetails), _) => onPrivate(privateKeeperDetails)
      case (_, Some(businessKeeperDetails)) => onBusiness(businessKeeperDetails)
      case _ => onError(KeeperDetailsNotInCacheMessage)
    }
  }

  private def openView(postcode: String)
                      (implicit request: Request[_]) = {
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        Ok(new_keeper_enter_address_manually(
          NewKeeperEnterAddressManuallyViewModel(form.fill(), vehicleDetails),
          postcode
        ))
      case _ => error(VehicleDetailsNotInCacheMessage)
    }
  }

  private def error(message: String): Result = {
    Logger.warn(message)
    Redirect(routes.VehicleLookup.present())
  }

  private def handleInvalidForm(invalidForm: Form[NewKeeperEnterAddressManuallyFormModel],
                                postcode: String)
                               (implicit request: Request[_]) = {
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleDetails) =>
        BadRequest(new_keeper_enter_address_manually(
          NewKeeperEnterAddressManuallyViewModel(formWithReplacedErrors(invalidForm), vehicleDetails),
          postcode)
        )
      case _ => error(VehicleDetailsNotInCacheMessage)
    }
  }

  private def handleValidForm(validForm: NewKeeperEnterAddressManuallyFormModel, postcode: String)
                             (implicit request: Request[_]): Result = {
    createNewKeeper(AddressModel.from(
      validForm.addressAndPostcodeModel,
      postcode
    )) match {
      case Some(keeperDetails) =>
        Redirect(routes.CompleteAndConfirm.present()).
          withCookie(validForm).
          withCookie(keeperDetails).
          withCookie(AllowGoingToCompleteAndConfirmPageCacheKey, "true")
      case _ => error("No new keeper details found in cache, redirecting to vehicle lookup")
    }
  }

  private def formWithReplacedErrors(form: Form[NewKeeperEnterAddressManuallyFormModel]) =
    form.replaceError(
      "addressAndPostcode.addressLines.buildingNameOrNumber",
      FormError("addressAndPostcode.addressLines", "error.address.buildingNameOrNumber.invalid")
    ).replaceError(
        "addressAndPostcode.addressLines.postTown",
        FormError("addressAndPostcode.addressLines", "error.address.postTown")
      ).replaceError(
        "addressAndPostcode.postcode",
        FormError("addressAndPostcode.postcode", "error.address.postcode.invalid")
      ).distinctErrors
}