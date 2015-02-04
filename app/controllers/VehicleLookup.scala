package controllers

import com.google.inject.Inject
import models.VehicleLookupViewModel
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.VehicleLookupResponseCodeCacheKey
import models.VehicleLookupFormModel.Form.{DocumentReferenceNumberId, VehicleRegistrationNumberId, VehicleSoldToId}
import models.{BusinessKeeperDetailsCacheKeys, PrivateKeeperDetailsCacheKeys}
import org.joda.time.DateTime
import play.api.data.{Form => PlayForm, FormError}
import play.api.mvc.{Action, Call, Request}
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.DmsWebEndUserDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.DmsWebHeaderDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.DmsWebHeaderDto
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichForm, RichResult}
import common.model.VehicleAndKeeperDetailsModel
import common.views.helpers.FormExtensions.formBinding
import common.controllers.VehicleLookupBase
import common.controllers.VehicleLookupBase.LookupResult
import common.controllers.VehicleLookupBase.VehicleFound
import common.controllers.VehicleLookupBase.VehicleNotFound
import common.services.DateService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsDto
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperDetailsRequest
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import utils.helpers.Config
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private

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
      dmsHeader = buildHeader(trackingId),
      referenceNumber = form.referenceNumber,
      registrationNumber = form.registrationNumber,
      transactionTimestamp = new DateTime
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
    val (call, discardedCookies) =
      if (soldTo == VehicleSoldTo_Private)
        (routes.PrivateKeeperDetails.present(), BusinessKeeperDetailsCacheKeys)
      else
        (routes.BusinessKeeperDetails.present(), PrivateKeeperDetailsCacheKeys)

    Redirect(call).
      discardingCookies(discardedCookies).
      withCookie(model)
  }

  private def buildHeader(trackingId: String): DmsWebHeaderDto = {
    val alwaysLog = true
    val englishLanguage = "EN"
    DmsWebHeaderDto(conversationId = trackingId,
      originDateTime = new DateTime,
      applicationCode = config.applicationCode,
      channelCode = config.channelCode,
      contactId = config.contactId,
      eventFlag = alwaysLog,
      serviceTypeCode = config.dmsServiceTypeCode,
      languageCode = englishLanguage,
      endUser = None)
  }
}