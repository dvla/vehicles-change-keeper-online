package controllers

import com.google.inject.Inject
import models.BusinessKeeperDetailsCacheKeys
import models.K2KCacheKeyPrefix.CookiePrefix
import models.PrivateKeeperDetailsCacheKeys
import models.SellerEmailModel
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.Form.DocumentReferenceNumberId
import models.VehicleLookupFormModel.Form.VehicleRegistrationNumberId
import models.VehicleLookupFormModel.Form.VehicleSellerEmail
import models.VehicleLookupFormModel.Form.VehicleSoldToId
import models.VehicleLookupViewModel
import models.VehicleLookupFormModel.{VehicleLookupResponseCodeCacheKey, Key, JsonFormat}
import play.api.data.{Form => PlayForm, FormError}
import play.api.mvc.{Result, Request}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import uk.gov.dvla.vehicles.presentation.common.model.{BruteForcePreventionModel, VehicleAndKeeperDetailsModel}
import common.views.helpers.FormExtensions.formBinding
import common.services.DateService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupBase1
import utils.helpers.Config
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private

import scala.concurrent.Future

class VehicleLookup @Inject()(implicit bruteForceService: BruteForcePreventionService,
                              vehicleLookupService: VehicleAndKeeperLookupService,
                              dateService: DateService,
                              clientSideSessionFactory: ClientSideSessionFactory,
                              config: Config) extends VehicleLookupBase1[VehicleLookupFormModel] {
  override val form = PlayForm(VehicleLookupFormModel.Form.Mapping)
  override val responseCodeCacheKey: String = VehicleLookupResponseCodeCacheKey

  override def vrmLocked(bruteForcePreventionModel: BruteForcePreventionModel, formModel: VehicleLookupFormModel)
                        (implicit request: Request[_]): Result =
    Redirect(routes.VrmLocked.present())

  override def microServiceError(t: Throwable, formModel: VehicleLookupFormModel)
                                (implicit request: Request[_]): Result =
    Redirect(routes.MicroServiceError.present())

  override def vehicleLookupFailure(responseCode: String, formModel: VehicleLookupFormModel)
                                   (implicit request: Request[_]): Result =
    Redirect(routes.VehicleLookupFailure.present())

  override def presentResult(implicit request: Request[_]) = Ok(
    views.html.changekeeper.vehicle_lookup(
    VehicleLookupViewModel(form.fill()))
  )

  override def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperDetailsDto, formModel: VehicleLookupFormModel)
                                (implicit request: Request[_]): Result = {
    val model = VehicleAndKeeperDetailsModel.from(vehicleAndKeeperDetailsDto)
    val emailCapture = SellerEmailModel(formModel.sellerEmail)
    val suppressed = model.suppressedV5Flag.getOrElse(false)

    val (call, discardedCookies) =
      (formModel.vehicleSoldTo, suppressed) match {
        case (_, true) => (routes.SuppressedV5C.present(), BusinessKeeperDetailsCacheKeys)
        case (VehicleSoldTo_Private, false) => (routes.PrivateKeeperDetails.present(), BusinessKeeperDetailsCacheKeys)
        case (_, false) => (routes.BusinessKeeperDetails.present(), PrivateKeeperDetailsCacheKeys)
      }

    Redirect(call).
      discardingCookies(discardedCookies).
      withCookie(model).
      withCookie(emailCapture)
  }

  override def invalidFormResult(invalidForm: PlayForm[VehicleLookupFormModel])
                                (implicit request: Request[_]): Future[Result] = Future.successful {
    BadRequest(
      views.html.changekeeper.vehicle_lookup(VehicleLookupViewModel(formWithReplacedErrors(invalidForm)))
    )
  }

  private def formWithReplacedErrors(form: PlayForm[VehicleLookupFormModel]) = {
    form.replaceError(
      VehicleRegistrationNumberId,
      FormError(key = VehicleRegistrationNumberId, message = "error.restricted.validVrnOnly", args = Seq.empty)
    ).replaceError(
        DocumentReferenceNumberId,
        FormError(key = DocumentReferenceNumberId, message = "error.validDocumentReferenceNumber", args = Seq.empty)
      ).replaceError(
        VehicleSoldToId,
        FormError(key = VehicleSoldToId, message = "error.validBougtByType", args = Seq.empty)
      ).replaceError(
        VehicleSellerEmail,
        FormError(key = VehicleSellerEmail, message = "error.validSellerEmail", args = Seq.empty)
      ).distinctErrors
  }
}
