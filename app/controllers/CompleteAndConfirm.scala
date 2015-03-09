package controllers

import com.google.inject.Inject
import email.{EmailSellerMessageBuilder, EmailMessageBuilder}
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.CompleteAndConfirmFormModel.Form.{ConsentId, MileageId}
import models.CompleteAndConfirmResponseModel
import models.CompleteAndConfirmViewModel
import models.K2KCacheKeyPrefix.CookiePrefix
import models.SellerEmailModel
import models.VehicleLookupFormModel
import models.VehicleNewKeeperCompletionCacheKeys
import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.ISODateTimeFormat
import play.api.data.{FormError, Form}
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Call, Controller, Request, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.mappings.TitleType
import common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.services.{SEND, DateService}
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.common.{VssWebEndUserDto, VssWebHeaderDto}
import common.webserviceclients.acquire.{AcquireRequestDto, AcquireResponseDto, AcquireService, KeeperDetailsDto, TitleTypeDto}
import utils.helpers.Config
import views.html.changekeeper.complete_and_confirm

class CompleteAndConfirm @Inject()(webService: AcquireService)(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                                               dateService: DateService,
                                                               config: Config) extends Controller {
  private val cookiesToBeDiscardedOnRedirectAway =
    VehicleNewKeeperCompletionCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private[controllers] val form = Form(
    CompleteAndConfirmFormModel.Form.detailMapping
  )

  private final val NoCookiesFoundMessage = "Failed to find new keeper details and or vehicle details in cache" +
    "Now redirecting to vehicle lookup"

  def present = Action { implicit request =>
    canPerform {
      val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
      val vehicleDetailsOpt = request.cookies.getModel[VehicleAndKeeperDetailsModel]
      (newKeeperDetailsOpt, vehicleDetailsOpt) match {
        case (Some(newKeeperDetails), Some(vehicleAndKeeperDetails)) =>
          Ok(complete_and_confirm(CompleteAndConfirmViewModel(form.fill(),
            vehicleAndKeeperDetails,
            newKeeperDetails,
            isSaleDateInvalid = false,
            isDateToCompareDisposalDate = false),
            dateService)
          )
        case _ =>
          redirectToVehicleLookup(NoCookiesFoundMessage).discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey)
      }
    }(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardedOnRedirectAway))
  }

  // The dates are valid if they are on the same date or if the disposal date/keeper change date is before the acquisition date
  def submitWithDateCheck = submitBase(
    (keeperEndDateOrChangeDate, dateOfSale) =>
      keeperEndDateOrChangeDate.toLocalDate.isEqual(dateOfSale) || keeperEndDateOrChangeDate.toLocalDate.isBefore(dateOfSale)
  )

  def submitNoDateCheck = submitBase((keeperEndDateOrChangeDate, dateOfSale) => true)

  private def submitBase(validDates: (DateTime, LocalDate) => Boolean) = Action.async { implicit request =>
    canPerform {
      form.bindFromRequest.fold(
        invalidForm => Future.successful {
          val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
          val vehicleDetailsOpt = request.cookies.getModel[VehicleAndKeeperDetailsModel]
          (newKeeperDetailsOpt, vehicleDetailsOpt) match {
            case (Some(newKeeperDetails), Some(vehicleDetails)) =>
              BadRequest(complete_and_confirm(
                CompleteAndConfirmViewModel(formWithReplacedErrors(invalidForm),
                  vehicleDetails,
                  newKeeperDetails,
                  isSaleDateInvalid = false,
                  isDateToCompareDisposalDate = false),
                dateService)
              )
            case _ =>
              Logger.warn("Could not find expected data in cache on dispose submit - now redirecting...")
              Redirect(routes.VehicleLookup.present()).discardingCookies()
          }
        },
        validForm => processValidForm(validDates, validForm)
      )
    }(Future.successful(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardedOnRedirectAway)))
  }

  private def processValidForm(validDates: (DateTime, LocalDate) => Boolean, validForm: CompleteAndConfirmFormModel)
                              (implicit request: Request[AnyContent]): Future[Result] = {
    val newKeeperDetailsOpt = request.cookies.getModel[NewKeeperDetailsViewModel]
    val vehicleLookupOpt = request.cookies.getModel[VehicleLookupFormModel]
    val vehicleDetailsOpt = request.cookies.getModel[VehicleAndKeeperDetailsModel]
    val sellerEmailModelOpt = request.cookies.getModel[SellerEmailModel]
    val validFormResult = (newKeeperDetailsOpt, vehicleLookupOpt, vehicleDetailsOpt, sellerEmailModelOpt) match {
      case (Some(newKeeperDetails), Some(vehicleLookup), Some(vehicleDetails), Some(sellerEmailModel)) =>
        Logger.debug(s"CompleteAndConfirm - keeperEndDate = ${vehicleDetails.keeperEndDate}")
        Logger.debug(s"CompleteAndConfirm - keeperChangeDate = ${vehicleDetails.keeperChangeDate}")
        // Only do the date check if the keeper end date or the keeper change date is present. If they are both
        // present or neither are present then skip the check
        Seq(vehicleDetails.keeperChangeDate, vehicleDetails.keeperEndDate).flatten match {
          case Seq(endDateOrChangeDate) if validDates(endDateOrChangeDate, validForm.dateOfSale) =>
            // Either the keeper end date or the keeper change date is populated so do the date check
            // The dateOfSale is valid
            acquireAction(
              validForm,
              newKeeperDetails,
              vehicleLookup,
              vehicleDetails,
              sellerEmailModel,
              request.cookies.trackingId
            )
          case Seq(endDateOrChangeDate) => Future.successful{
            // The dateOfSale is invalid
            BadRequest(complete_and_confirm(
              CompleteAndConfirmViewModel(form.fill(validForm),
                vehicleDetails,
                newKeeperDetails,
                isSaleDateInvalid = true, // This will tell the page to display the date warning
                isDateToCompareDisposalDate = vehicleDetails.keeperEndDate.isDefined,
                submitAction = controllers.routes.CompleteAndConfirm.submitNoDateCheck(), // Next time the submit will not perform any date check
                dateToCompare = Some(endDateOrChangeDate.toString("dd/MM/yyyy")) // Pass the dateOfDisposal/change date so we can tell the user in the warning
              ), dateService)
            )
          }
          case _ =>
            // Either both dates are missing or they are both populated so just call the acquire service
            // and move to the next page
            acquireAction(
              validForm,
              newKeeperDetails,
              vehicleLookup,
              vehicleDetails,
              sellerEmailModel,
              request.cookies.trackingId
            )
        }
      case _ => Future.successful {
        Logger.warn("Did not find expected cookie data on complete and confirm submit - now redirecting to VehicleLookup...")
        Redirect(routes.VehicleLookup.present()).discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey)
      }
    }
    validFormResult
  }

  private def redirectToVehicleLookup(message: String) = {
    Logger.warn(message)
    Redirect(routes.VehicleLookup.present())
  }

  def back = Action { implicit request =>
    request.cookies.getModel[NewKeeperEnterAddressManuallyFormModel] match {
      case Some(manualAddress) =>
        Redirect(routes.NewKeeperEnterAddressManually.present())
      case None => Redirect(routes.NewKeeperChooseYourAddress.present())
    }
  }

  private def formWithReplacedErrors(form: Form[CompleteAndConfirmFormModel]) = {
    form.replaceError(
      ConsentId,
      "error.required",
      FormError(key = ConsentId, message = "change_keeper_keeperdetailscomplete.consentError", args = Seq.empty)
    ).replaceError(
        MileageId,
        "error.number",
        FormError(key = MileageId, message = "change_keeper_privatekeeperdetailscomplete.mileage.validation", args = Seq.empty)
      ).distinctErrors
  }

  private def acquireAction(completeAndConfirmForm: CompleteAndConfirmFormModel,
                            newKeeperDetailsView: NewKeeperDetailsViewModel,
                            vehicleLookup: VehicleLookupFormModel,
                            vehicleDetails: VehicleAndKeeperDetailsModel,
                            sellerEmailModel: SellerEmailModel,
                            trackingId: String)
                           (implicit request: Request[AnyContent]): Future[Result] = {

    val transactionTimestamp = dateService.now.toDateTime

    val disposeRequest = buildMicroServiceRequest(vehicleLookup, completeAndConfirmForm,
      newKeeperDetailsView, transactionTimestamp, trackingId)
    webService.invoke(disposeRequest, trackingId).map {
      case (httpResponseCode, response) =>
        Some(Redirect(nextPage(httpResponseCode, response)(vehicleDetails, newKeeperDetailsView, sellerEmailModel)))
          .map(_.withCookie(CompleteAndConfirmResponseModel(response.get.transactionId, transactionTimestamp)))
          .map(_.withCookie(completeAndConfirmForm))
          .get
    }.recover {
      case e: Throwable =>
        Logger.warn(s"Acquire micro-service call failed.", e)
        Redirect(routes.MicroServiceError.present())
    }
  }

  def nextPage(httpResponseCode: Int, response: Option[AcquireResponseDto])
              (vehicleDetails: VehicleAndKeeperDetailsModel,
               keeperDetails: NewKeeperDetailsViewModel,
                sellerEmailModel: SellerEmailModel) =
    response match {
      case Some(r) if r.responseCode.isDefined => successReturn (vehicleDetails, keeperDetails, sellerEmailModel) //handleResponseCode(r.responseCode.get)
      case _ => handleHttpStatusCode(httpResponseCode)(vehicleDetails, keeperDetails, sellerEmailModel)
    }

  def buildMicroServiceRequest(vehicleLookup: VehicleLookupFormModel,
                               completeAndConfirmFormModel: CompleteAndConfirmFormModel,
                               newKeeperDetailsViewModel: NewKeeperDetailsViewModel,
                               timestamp: DateTime,
                               trackingId: String): AcquireRequestDto = {

    val newKeeperDetails = buildKeeperDetails(newKeeperDetailsViewModel)

    val dateTimeFormatter = ISODateTimeFormat.dateTime()

    AcquireRequestDto(buildWebHeader(trackingId),
      vehicleLookup.referenceNumber,
      vehicleLookup.registrationNumber,
      newKeeperDetails,
      None,
      None,
      dateTimeFormatter.print(completeAndConfirmFormModel.dateOfSale.toDateTimeAtStartOfDay),
      completeAndConfirmFormModel.mileage,
      checkboxValueToBoolean(completeAndConfirmFormModel.consent),
      dateTimeFormatter.print(timestamp),
      requiresSorn = false
    )
  }

  private def buildTitle (titleType: Option[TitleType]): TitleTypeDto = {
    titleType match {
      case Some(title) => title.other match {
        case "" => TitleTypeDto(Some(title.titleType), None)
        case _ => TitleTypeDto(Some(title.titleType), Some(title.other))
      }
      case None => TitleTypeDto(None, None)
    }
  }

  def buildKeeperDetails(newKeeperDetailsViewModel: NewKeeperDetailsViewModel) :KeeperDetailsDto = {
    val keeperAddress = newKeeperDetailsViewModel.address.address

    val dateOfBirth = newKeeperDetailsViewModel.dateOfBirth match {
      case Some(date) => Some(date.toDateTimeAtStartOfDay.toString)
      case _ => None
    }

    KeeperDetailsDto(buildTitle(newKeeperDetailsViewModel.title),
      newKeeperDetailsViewModel.businessName,
      newKeeperDetailsViewModel.firstName,
      newKeeperDetailsViewModel.lastName,
      dateOfBirth,
      getAddressLines(keeperAddress, 4),
      getPostTownFromAddress(keeperAddress).getOrElse(""),
      getPostCodeFromAddress(keeperAddress).getOrElse(""),
      newKeeperDetailsViewModel.email,
      newKeeperDetailsViewModel.driverNumber)
  }

  def handleHttpStatusCode(statusCode: Int)
                          (vehicleDetails: VehicleAndKeeperDetailsModel,
                           keeperDetails: NewKeeperDetailsViewModel,
                           sellerEmailModel: SellerEmailModel): Call =
    statusCode match {
      case OK =>
        successReturn (vehicleDetails, keeperDetails, sellerEmailModel)
      case _ =>
        routes.MicroServiceError.present()
    }

  private def successReturn(vehicleDetails: VehicleAndKeeperDetailsModel,
                            keeperDetails: NewKeeperDetailsViewModel,
                            sellerEmailModel: SellerEmailModel): Call = {
    //send the email
    createAndSendSellerEmail(vehicleDetails, sellerEmailModel.email)
    createAndSendEmail(vehicleDetails, keeperDetails)
    //redirect
    routes.ChangeKeeperSuccess.present()
  }

  private def checkboxValueToBoolean (checkboxValue: String): Boolean = {
    checkboxValue == "true"
  }

  private def getPostCodeFromAddress (address: Seq[String]): Option[String] = {
    Option(address.last.replace(" ",""))
  }

  private def getPostTownFromAddress (address: Seq[String]): Option[String] = {
    Option(address.takeRight(2).head)
  }

  private def getAddressLines(address: Seq[String], lines: Int): Seq[String] = {
    val excludeLines = 2
    val getLines = if (lines <= address.length - excludeLines) lines else address.length - excludeLines
    address.take(getLines)
  }

  private def buildWebHeader(trackingId: String): VssWebHeaderDto = {
    VssWebHeaderDto(transactionId = trackingId,
    originDateTime = new DateTime,
    applicationCode = config.applicationCode,
    serviceTypeCode = config.vssServiceTypeCode,
    buildEndUser())
  }

  private def buildEndUser(): VssWebEndUserDto = {
     VssWebEndUserDto(endUserId = config.orgBusinessUnit, orgBusUnit = config.orgBusinessUnit)
  }

  /**
   * Checks the presence of <code>AllowGoingToCompleteAndConfirmPageCacheKey</code> to allow the completion of
   * the request, otherwise calls redirect function.
   *
   * Example:
   * def present = Action { implicit request =>
   *  canPerform {
   *    Ok("success")
   *  }(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway))
   * }
   *
   * or
   * def present = Action.async { implicit request =>
   *  canPerform {
   *    ...
   *  }(Future.successful(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardeOnRedirectAway)))
   * }
   *
   * @param action the action body
   * @param redirect the Result function to be called if the cookie is not present
   * @param request implicit request
   * @tparam T for the purposes of the application this should be Either a Result or a Future[Result]
   * @return T by either calling the action or the redirect
   */
  private def canPerform[T](action: => T)(redirect: => T)
                           (implicit request: Request[_])= {
    request.cookies.getString(AllowGoingToCompleteAndConfirmPageCacheKey).fold {
      Logger.warn(s"Could not find AllowGoingToCompleteAndConfirmPageCacheKey in the request. " +
        s"Redirect to starting page discarding cookies")
      redirect
    }(c => action)
  }

  /**
   * Calling this method on a successful submission, will send an email if we have the new keeper details.
   * @param keeperDetails the keeper model from the cookie.
   * @return
   */
  private def createAndSendEmail(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel) =
    keeperDetails.email match {
      case Some(emailAddr) =>
        import scala.language.postfixOps

        import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

        implicit val emailConfiguration = config.emailConfiguration
        val template = EmailMessageBuilder.buildWith(vehicleDetails, keeperDetails)

        // This sends the email.
        SEND email template withSubject vehicleDetails.registrationNumber to emailAddr send

      case None => Logger.info("Tried to send an email with no keeper details")
    }

  private def createAndSendSellerEmail(vehicleDetails: VehicleAndKeeperDetailsModel, sellerEmail: Option[String]) =
    sellerEmail match {
      case Some(emailAddr) =>
        import scala.language.postfixOps

        import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

        implicit val emailConfiguration = config.emailConfiguration
        val template = EmailSellerMessageBuilder.buildWith(vehicleDetails)

        // This sends the email.
        SEND email template withSubject vehicleDetails.registrationNumber to emailAddr send
      case None => Logger.info("Tried to send a receipt to seller but no email was found")
    }
}