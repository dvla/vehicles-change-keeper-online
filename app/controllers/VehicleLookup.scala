package controllers

import models.BusinessKeeperDetailsCacheKeys
import models.PrivateKeeperDetailsCacheKeys
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import play.Logger
import play.api.mvc.Call
import models.VehicleLookupFormModel.Form.{DocumentReferenceNumberId, VehicleRegistrationNumberId, VehicleSoldToId}
import com.google.inject.Inject
import play.api.data.{Form => PlayForm, FormError}
import play.api.mvc.Action
import play.api.mvc.Request
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import scala.concurrent.ExecutionContext.Implicits.global
import common.views.helpers.FormExtensions.formBinding
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupBase
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupBase.LookupResult
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupBase.VehicleFound
import uk.gov.dvla.vehicles.presentation.common.controllers.VehicleLookupBase.VehicleNotFound
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import utils.helpers.Config
import models.{VehicleLookupFormModel, VehicleLookupViewModel}
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private

import scala.concurrent.Future


class VehicleLookup @Inject()(val bruteForceService: BruteForcePreventionService,
                              vehicleLookupService: VehicleAndKeeperLookupService,
                              dateService: DateService)
                             (implicit val clientSideSessionFactory: ClientSideSessionFactory,
                              config: Config) extends VehicleLookupBase {
  // TODO : Redirect to the correct page
  override val vrmLocked: Call = routes.VrmLocked.present()
  override val microServiceError: Call = routes.MicroServiceError.present()
  // TODO : Redirect to the correct page
  override val vehicleLookupFailure: Call = routes.VehicleLookupFailure.present()
  override val responseCodeCacheKey: String = VehicleLookupResponseCodeCacheKey

  override type Form = VehicleLookupFormModel

  private[controllers] val form = PlayForm(
    VehicleLookupFormModel.Form.Mapping
  )

  def present = Action { implicit request =>
    Ok(views.html.changekeeper.vehicle_lookup(
      VehicleLookupViewModel(form.fill()))
    )
  }

  def submit = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm =>
        Future {
              BadRequest(views.html.changekeeper.vehicle_lookup(
                VehicleLookupViewModel(formWithReplacedErrors(invalidForm)))
              )
        },
      validForm => {
        bruteForceAndLookup(
          validForm.registrationNumber,
          validForm.referenceNumber,
          validForm
        )
      }
    )
  }

  private def formWithReplacedErrors(form: PlayForm[VehicleLookupFormModel]) = {
    form.replaceError(
      VehicleRegistrationNumberId, FormError(
        key = VehicleRegistrationNumberId, message = "error.restricted.validVrnOnly", args = Seq.empty)
    ).replaceError(
        DocumentReferenceNumberId,FormError(
          key = DocumentReferenceNumberId, message = "error.validDocumentReferenceNumber", args = Seq.empty)
      ).replaceError(
        VehicleSoldToId,FormError(
          key = VehicleSoldToId, message = "error.validBougtByType", args = Seq.empty)
      ).distinctErrors
  }

  override protected def callLookupService(trackingId: String, form: Form)(implicit request: Request[_]): Future[LookupResult] = {
    val vehicleAndKeeperDetailsRequest = VehicleAndKeeperDetailsRequest(
      referenceNumber = form.referenceNumber,
      registrationNumber = form.registrationNumber
    )

    vehicleLookupService.invoke(vehicleAndKeeperDetailsRequest, trackingId) map { response =>
      response.responseCode match {
        case Some(responseCode) =>
          VehicleNotFound(responseCode)
        case None =>
          response.vehicleAndKeeperDetailsDto match {
            case Some(dto) => VehicleFound(vehicleFoundResult(dto, form.vehicleSoldTo))
            case None => throw new RuntimeException("No vehicleDetailsDto found")
          }
      }
    }
  }

  private def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperDetailsDto, soldTo: String)(implicit request: Request[_]) = {
    val model = VehicleAndKeeperDetailsModel.from(vehicleAndKeeperDetailsDto)

    soldTo match {
      case "Private" => Redirect(routes.PrivateKeeperDetails.present()).withCookie(model)
      case "Business" => Redirect(routes.BusinessKeeperDetails.present()).withCookie(model)
      case _ => Redirect(routes.VehicleLookup.present())
    }
  }
}