package controllers

import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Writes
import play.api.mvc._
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.DmsWebHeaderDto
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.{VehicleAndKeeperDetailsDto, VehicleAndKeeperDetailsRequest, VehicleAndKeeperLookupService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieImplicits.{RichCookies, RichResult}
import common.clientsidesession.{CacheKey, ClientSideSessionFactory}
import common.controllers.VehicleLookupBase.{LookupResult, VehicleFound, VehicleNotFound}
import common.LogFormats
import uk.gov.dvla.vehicles.presentation.common.model.{CacheKeyPrefix, BruteForcePreventionModel}
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService

trait VehicleLookupFormModelBase {
  val referenceNumber: String
  val registrationNumber: String
}

trait VehicleLookupConfig {
  def applicationCode: String
  def vssServiceTypeCode: String
  def dmsServiceTypeCode: String
  def orgBusinessUnit: String
  def channelCode: String
  def contactId: Long
}

abstract class VehicleLookupBase1[FormModel <: VehicleLookupFormModelBase]
                (implicit vehicleLookupService: VehicleAndKeeperLookupService,
                 config: VehicleLookupConfig,
                 bruteForceService: BruteForcePreventionService,
                 clientSideSessionFactory: ClientSideSessionFactory,
                 toJson: Writes[FormModel],
                 cacheKey: CacheKey[FormModel],
                 cacheKeyPrefix: CacheKeyPrefix) extends Controller {
  def vrmLocked: Result
  def vehicleLookupFailure: Result
  def microServiceError: Result
  def invalidFormResult(invalidForm: play.api.data.Form[FormModel])(implicit request: Request[_]): Future[Result]
  def presentResult(implicit request: Request[_]): Result
  def vehicleFoundResult(vehicleAndKeeperDetailsDto: VehicleAndKeeperDetailsDto,
                         validFormModel: FormModel)(implicit request: Request[_]): Result

  val responseCodeCacheKey: String
  val form: play.api.data.Form[FormModel]

  def present = Action { implicit request => presentResult}

  def submit = Action.async { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => invalidFormResult(invalidForm),
      validFormModel => bruteForceAndLookup(validFormModel)
    )
  }

  private def bruteForceAndLookup(formModel: FormModel)
                         (implicit request: Request[_]): Future[Result] =
    bruteForceService.isVrmLookupPermitted(formModel.registrationNumber).flatMap { bruteForcePreventionModel =>
      val resultFuture = if (bruteForcePreventionModel.permitted)
        lookupVehicle(formModel.registrationNumber, formModel.referenceNumber, bruteForcePreventionModel, formModel)
      else Future.successful {
        val anonRegistrationNumber = LogFormats.anonymize(formModel.registrationNumber)
        Logger.warn(s"BruteForceService locked out vrm: $anonRegistrationNumber")
        vrmLocked
      }

      resultFuture.map { result =>
        import BruteForcePreventionModel.{key, JsonFormat}
        result.withCookie(bruteForcePreventionModel)(JsonFormat, key(cacheKeyPrefix), request, clientSideSessionFactory)
      }

    } recover {
      case exception: Throwable =>
        Logger.error(
          s"Exception thrown by BruteForceService so for safety we won't let anyone through. " +
            s"Exception:\n${exception.getMessage}\n${exception.getStackTraceString}"
        )
        microServiceError
    } map { result =>
      result.withCookie(formModel)
    }

  private def lookupVehicle(registrationNumber: String,
                            referenceNumber: String,
                            bruteForcePreventionModel: BruteForcePreventionModel,
                            form: FormModel)
                           (implicit request: Request[_]): Future[Result] = {
    def notFound(responseCode: String): Result = {
      Logger.debug(s"VehicleAndKeeperLookup encountered a problem with request" +
        s" ${LogFormats.anonymize(referenceNumber)}" +
        s" ${LogFormats.anonymize(registrationNumber)}," +
        s" redirect to VehicleAndKeeperLookupFailure")
      vehicleLookupFailure.
        withCookie(responseCodeCacheKey, responseCode)
    }

    callLookupService(request.cookies.trackingId(), form).map {
      case VehicleNotFound(responseCode) => notFound(responseCode)
      case VehicleFound(result) =>
        bruteForceService.reset(registrationNumber).onComplete {
          case Success(httpCode) => Logger.debug(s"Brute force reset was called - it returned httpCode: $httpCode")
          case Failure(t) => Logger.error(s"Brute force reset failed: ${t.getStackTraceString}")
        }
        result
    } recover {
      case NonFatal(e) =>
        microServiceErrorResult("Lookup web service call failed.", e)
    }
  }

  protected def callLookupService(trackingId: String, formModel: FormModel)
                                 (implicit request: Request[_]): Future[LookupResult] = {
    val vehicleAndKeeperDetailsRequest = VehicleAndKeeperDetailsRequest(
      dmsHeader = buildHeader(trackingId),
      referenceNumber = formModel.referenceNumber,
      registrationNumber = formModel.registrationNumber,
      transactionTimestamp = new DateTime
    )

    vehicleLookupService.invoke(vehicleAndKeeperDetailsRequest, trackingId) map { response =>
      response.responseCode match {
        case Some(responseCode) =>
          VehicleNotFound(responseCode)
        case None =>
          response.vehicleAndKeeperDetailsDto match {
            case Some(dto) => VehicleFound(vehicleFoundResult(dto, formModel))
            case None => throw new RuntimeException("No vehicleDetailsDto found")
          }
      }
    }
  }

  private def microServiceErrorResult(message: String, exception: Throwable): Result = {
    Logger.error(message, exception)
    microServiceError
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

private object VehicleLookupBase1 {
  sealed trait LookupResult

  final case class VehicleNotFound(responseCode: String) extends LookupResult

  final case class VehicleFound(result: Result) extends LookupResult
}
