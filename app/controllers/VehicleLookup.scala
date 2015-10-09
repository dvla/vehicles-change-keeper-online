package controllers

import com.google.inject.Inject
import models.BusinessKeeperDetailsCacheKeys
import models.K2KCacheKeyPrefix.CookiePrefix
import models.PrivateKeeperDetailsCacheKeys
import models.SellerEmailModel
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.JsonFormat
import models.VehicleLookupFormModel.Key
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import models.VehicleLookupFormModel.Form.DocumentReferenceNumberId
import models.VehicleLookupFormModel.Form.VehicleRegistrationNumberId
import models.VehicleLookupFormModel.Form.VehicleSoldToId
import models.VehicleLookupViewModel
import play.api.data.{Form => PlayForm, FormError}
import play.api.mvc.{Call, Request, Result}
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichResult, RichCookies}
import common.controllers.VehicleLookupBase
import common.model.{BruteForcePreventionModel, VehicleAndKeeperDetailsModel}
import common.services.DateService
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupFailureResponse
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupDetailsDto
import utils.helpers.Config
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private

class VehicleLookup @Inject()(implicit bruteForceService: BruteForcePreventionService,
                              vehicleLookupService: VehicleAndKeeperLookupService,
                              dateService: DateService,
                              clientSideSessionFactory: ClientSideSessionFactory,
                              config: Config) extends VehicleLookupBase[VehicleLookupFormModel] {
  override val form = PlayForm(VehicleLookupFormModel.Form.Mapping)
  override val responseCodeCacheKey: String = VehicleLookupResponseCodeCacheKey

  override def vrmLocked(bruteForcePreventionModel: BruteForcePreventionModel, formModel: VehicleLookupFormModel)
                        (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.VrmLocked.present()}")
    Redirect(routes.VrmLocked.present())
  }

  override def microServiceError(t: Throwable, formModel: VehicleLookupFormModel)
                                (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.MicroServiceError.present()}")
    Redirect(routes.MicroServiceError.present())
  }

  override def vehicleLookupFailure(failure: VehicleAndKeeperLookupFailureResponse, formModel: VehicleLookupFormModel)
                                   (implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.VehicleLookupFailure.present()}")
    Redirect(routes.VehicleLookupFailure.present())
  }

  override def presentResult(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Info, "Presenting vehicle lookup view")
    Ok(views.html.changekeeper.vehicle_lookup(VehicleLookupViewModel(form.fill())))
  }

  override def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperLookupDetailsDto,
                                  formModel: VehicleLookupFormModel)
                                 (implicit request: Request[_]): Result = {
    val model = VehicleAndKeeperDetailsModel.from(vehicleAndKeeperDetailsDto)
    val emailCapture = SellerEmailModel(formModel.sellerEmail)
    val suppressed = model.suppressedV5Flag.getOrElse(false)

    val (call: Call, discardedCookies: Set[String]) =
      (formModel.vehicleSoldTo, suppressed) match {
        case (_, true) =>
          logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.SuppressedV5C.present()}")
          (routes.SuppressedV5C.present(), BusinessKeeperDetailsCacheKeys)

        case (VehicleSoldTo_Private, false) =>
          logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.PrivateKeeperDetails.present()}")
          (routes.PrivateKeeperDetails.present(), BusinessKeeperDetailsCacheKeys)

        case (_, false) =>
          logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.BusinessKeeperDetails.present()}")
          (routes.BusinessKeeperDetails.present(), PrivateKeeperDetailsCacheKeys)
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
      ).distinctErrors
  }
}
