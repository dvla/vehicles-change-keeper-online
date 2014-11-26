package controllers

import com.google.inject.Inject
import models.CompleteAndConfirmFormModel._
import models._
import models.CompleteAndConfirmFormModel.Form.{MileageId, ConsentId}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.data.{FormError, Form}
import play.api.mvc.{Action, AnyContent, Call, Controller, Request, Result}
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.mappings.TitleType
import uk.gov.dvla.vehicles.presentation.common.model.{VehicleAndKeeperDetailsModel, VehicleDetailsModel, TraderDetailsModel}
import common.services.DateService
import common.views.helpers.FormExtensions.formBinding
import utils.helpers.Config
import views.html.changekeeper.complete_and_confirm
import scala.Some
import play.api.mvc.Result
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import play.api.mvc.Call
import scala.Some
import models.CompleteAndConfirmViewModel
import play.api.mvc.Result
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import play.api.mvc.Call
import views.html.changekeeper.complete_and_confirm
import scala.Some
import models.CompleteAndConfirmViewModel
import play.api.mvc.Result

class CompleteAndConfirm @Inject()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                                               dateService: DateService,
                                                               config: Config) extends Controller {
  private val cookiesToBeDiscardeOnRedirectAway =
    VehicleNewKeeperCompletionCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private[controllers] val form = Form(
    CompleteAndConfirmFormModel.Form.Mapping
  )

  private final val NoCookiesFoundMessage = "Failed to find new keeper details and or vehicle details and or " +
    "vehicle sorn details in cache. Now redirecting to vehicle lookup"

  def present = Action { implicit request =>
    canPerformPresent {
      val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
      val vehicleDetailsOpt = request.cookies.getModel[VehicleAndKeeperDetailsModel]
      (newKeeperDetailsOpt, vehicleDetailsOpt) match {
        case (Some(newKeeperDetails), Some(vehicleAndKeeperDetails)) =>
          Ok(complete_and_confirm(CompleteAndConfirmViewModel(form.fill(), vehicleAndKeeperDetails, newKeeperDetails), dateService))
        case _ =>
          redirectToVehicleLookup(NoCookiesFoundMessage).discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey)
      }
    }
  }

  def submit = Action { implicit request =>
    Ok("Summary")
  }

//  def submit = Action.async { implicit request =>
//    canPerformSubmit {
//      form.bindFromRequest.fold(
//        invalidForm => Future.successful {
//          val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
//          val vehicleDetailsOpt = request.cookies.getModel[VehicleDetailsModel]
//          val vehicleSornOpt = request.cookies.getModel[VehicleTaxOrSornFormModel]
//          (newKeeperDetailsOpt, vehicleDetailsOpt, vehicleSornOpt) match {
//            case (Some(newKeeperDetails), Some(vehicleDetails), Some(vehicleSorn)) =>
//              BadRequest(complete_and_confirm(
//                CompleteAndConfirmViewModel(formWithReplacedErrors(invalidForm), vehicleDetails, newKeeperDetails, vehicleSorn), dateService)
//              )
//            case _ =>
//              Logger.warn("Could not find expected data in cache on dispose submit - now redirecting...")
//              Redirect(routes.VehicleLookup.present()).discardingCookies()
//          }
//        },
//        validForm => {
//          val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
//          val vehicleLookupOpt = request.cookies.getModel[VehicleLookupFormModel]
//          val vehicleDetailsOpt = request.cookies.getModel[VehicleDetailsModel]
//          val traderDetailsOpt = request.cookies.getModel[TraderDetailsModel]
//          val taxOrSornOpt = request.cookies.getModel[VehicleTaxOrSornFormModel]
//          val validFormResult = (newKeeperDetailsOpt, vehicleLookupOpt, vehicleDetailsOpt, traderDetailsOpt, taxOrSornOpt) match {
//            case (Some(newKeeperDetails), Some(vehicleLookup), Some(vehicleDetails), Some(traderDetails), Some(taxOrSorn)) =>
//              acquireAction(validForm,
//                newKeeperDetails,
//                vehicleLookup,
//                vehicleDetails,
//                traderDetails,
//                taxOrSorn,
//                request.cookies.trackingId())
//            case (_, _, _, None, _) => Future.successful {
//              Logger.warn("Could not find either dealer details in cache on Acquire submit - " +
//                "now redirecting to SetUpTradeDetails...")
//              Redirect(routes.SetUpTradeDetails.present())
//            }
//            case _ => Future.successful {
//              Logger.warn("Could not find expected data in cache on dispose submit - now redirecting to VehicleLookup...")
//              Redirect(routes.VehicleLookup.present())
//            }
//          }
//          validFormResult.map(_.discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey))
//        }
//      )
//    }
//  }

  private def canPerformPresent[R](action: => Result)(implicit request: Request[_]) =
    request.cookies.getString(AllowGoingToCompleteAndConfirmPageCacheKey).fold {
      Logger.warn(s"Could not find AllowGoingToCompleteAndConfirmPageCacheKey in the request. " +
        s"Redirect to VehicleLookup discarding cookies $cookiesToBeDiscardeOnRedirectAway")
      Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway)
    }(c => action)

  private def canPerformSubmit[R](action: => Future[Result])(implicit request: Request[_]) =
    request.cookies.getString(AllowGoingToCompleteAndConfirmPageCacheKey).fold {
      Logger.warn(s"Could not find AllowGoingToCompleteAndConfirmPageCacheKey in the request. " +
        s"Redirect to VehicleLookup discarding cookies $cookiesToBeDiscardeOnRedirectAway")
      Future.successful(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway))
    }(c => action)

  private def redirectToVehicleLookup(message: String) = {
    Logger.warn(message)
    Redirect(routes.VehicleLookup.present())
  }

  def back = Action { implicit request =>
    request.cookies.getModel[NewKeeperDetailsViewModel] match {
      case Some(keeperDetails) =>
        if (keeperDetails.address.uprn.isDefined) Redirect(routes.NewKeeperChooseYourAddress.present())
        else Redirect(routes.NewKeeperEnterAddressManually.present())
      case None => Redirect(routes.VehicleLookup.present())
    }
  }

  private def formWithReplacedErrors(form: Form[CompleteAndConfirmFormModel]) = {
    form.replaceError(
      ConsentId,
      "error.required",
      FormError(key = ConsentId, message = "acquire_keeperdetailscomplete.consentError", args = Seq.empty)
    ).replaceError(
        MileageId,
        "error.number",
        FormError(key = MileageId, message = "acquire_privatekeeperdetailscomplete.mileage.validation", args = Seq.empty)
      ).distinctErrors
  }

//  private def acquireAction(completeAndConfirmForm: CompleteAndConfirmFormModel,
//                            newKeeperDetailsView: NewKeeperDetailsViewModel,
//                            vehicleLookup: VehicleLookupFormModel,
//                            vehicleDetails: VehicleDetailsModel,
//                            traderDetails: TraderDetailsModel,
//                            taxOrSorn: VehicleTaxOrSornFormModel,
//                            trackingId: String)
//                           (implicit request: Request[AnyContent]): Future[Result] = {
//
//    val transactionTimestamp = dateService.now.toDateTime
//
//    val disposeRequest = buildMicroServiceRequest(vehicleLookup, completeAndConfirmForm,
//      newKeeperDetailsView, traderDetails, taxOrSorn, transactionTimestamp)
//
//    webService.invoke(disposeRequest, trackingId).map {
//      case (httpResponseCode, response) =>
//        Some(Redirect(nextPage(httpResponseCode, response)))
//          .map(_.withCookie(CompleteAndConfirmResponseModel(response.get.transactionId, transactionTimestamp)))
//          .map(_.withCookie(completeAndConfirmForm))
//          .get
//    }.recover {
//      case e: Throwable =>
//        Logger.warn(s"Acquire micro-service call failed.", e)
//        Redirect(routes.MicroServiceError.present())
//    }
//  }

//  def nextPage(httpResponseCode: Int, response: Option[AcquireResponseDto]) =
//    response match {
//      case Some(r) if r.responseCode.isDefined => handleResponseCode(r.responseCode.get)
//      case _ => handleHttpStatusCode(httpResponseCode)
//    }
//
//  def buildMicroServiceRequest(vehicleLookup: VehicleLookupFormModel,
//                               completeAndConfirmFormModel: CompleteAndConfirmFormModel,
//                               newKeeperDetailsViewModel: NewKeeperDetailsViewModel,
//                               traderDetailsModel: TraderDetailsModel, taxOrSornModel: VehicleTaxOrSornFormModel,
//                               timestamp: DateTime): AcquireRequestDto = {
//
//    val keeperDetails = buildKeeperDetails(newKeeperDetailsViewModel)
//
//    val traderAddress = traderDetailsModel.traderAddress.address
//    val traderDetails = TraderDetailsDto(traderOrganisationName = traderDetailsModel.traderName,
//      traderAddressLines = getAddressLines(traderAddress, 4),
//      traderPostTown = getPostTownFromAddress(traderAddress).getOrElse(""),
//      traderPostCode = getPostCodeFromAddress(traderAddress).getOrElse(""),
//      traderEmailAddress = traderDetailsModel.traderEmail)
//
//    val dateTimeFormatter = ISODateTimeFormat.dateTime()
//
//    AcquireRequestDto(referenceNumber = vehicleLookup.referenceNumber,
//      registrationNumber = vehicleLookup.registrationNumber,
//      keeperDetails,
//      traderDetails,
//      fleetNumber = newKeeperDetailsViewModel.fleetNumber,
//      dateOfTransfer = dateTimeFormatter.print(completeAndConfirmFormModel.dateOfSale.toDateTimeAtStartOfDay),
//      mileage = completeAndConfirmFormModel.mileage,
//      keeperConsent = checkboxValueToBoolean(completeAndConfirmFormModel.consent),
//      transactionTimestamp = dateTimeFormatter.print(timestamp),
//      requiresSorn = checkboxValueToBoolean(taxOrSornModel.sornVehicle.getOrElse("false"))
//    )
//  }

//  private def buildTitle (titleType: Option[TitleType]): TitleTypeDto = {
//    titleType match {
//      case Some(title) => title.other match {
//        case "" => TitleTypeDto(Some(title.titleType), None)
//        case _ => TitleTypeDto(Some(title.titleType), Some(title.other))
//      }
//      case None => TitleTypeDto(None, None)
//    }
//  }

//  def buildKeeperDetails(newKeeperDetailsViewModel: NewKeeperDetailsViewModel) :KeeperDetailsDto = {
//    val keeperAddress = newKeeperDetailsViewModel.address.address
//
//    val dateOfBirth = newKeeperDetailsViewModel.dateOfBirth match {
//      case Some(date) => Some(date.toDateTimeAtStartOfDay.toString)
//      case _ => None
//    }
//
//    KeeperDetailsDto(keeperTitle = buildTitle(newKeeperDetailsViewModel.title),
//      KeeperBusinessName = newKeeperDetailsViewModel.businessName,
//      keeperForename = newKeeperDetailsViewModel.firstName,
//      keeperSurname = newKeeperDetailsViewModel.lastName,
//      keeperDateOfBirth = dateOfBirth,
//      keeperAddressLines = getAddressLines(keeperAddress, 4),
//      keeperPostTown = getPostTownFromAddress(keeperAddress).getOrElse(""),
//      keeperPostCode = getPostCodeFromAddress(keeperAddress).getOrElse(""),
//      keeperEmailAddress = newKeeperDetailsViewModel.email,
//      keeperDriverNumber = newKeeperDetailsViewModel.driverNumber)
//  }

//  def handleResponseCode(acquireResponseCode: String): Call =
//    acquireResponseCode match {
//      case "ms.vehiclesService.error.generalError" =>
//        Logger.warn("Acquire soap endpoint redirecting to acquire failure page")
//        routes.AcquireFailure.present()
//      case _ =>
//        Logger.warn(s"Acquire micro-service failed so now redirecting to micro service error page. " +
//          s"Code returned from ms was $acquireResponseCode")
//        routes.MicroServiceError.present()
//    }

//  def handleHttpStatusCode(statusCode: Int): Call =
//    statusCode match {
//      case OK =>
//        routes.AcquireSuccess.present()
//      case _ =>
//        routes.MicroServiceError.present()
//    }

  def checkboxValueToBoolean (checkboxValue: String): Boolean = {
    checkboxValue == "true"
  }

  def getPostCodeFromAddress (address: Seq[String]): Option[String] = {
    Option(address.last.replace(" ",""))
  }

  def getPostTownFromAddress (address: Seq[String]): Option[String] = {
    Option(address.takeRight(2).head)
  }

  def getAddressLines(address: Seq[String], lines: Int): Seq[String] = {
    val excludeLines = 2
    val getLines = if (lines <= address.length - excludeLines) lines else address.length - excludeLines
    address.take(getLines)
  }
}