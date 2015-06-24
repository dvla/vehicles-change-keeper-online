package controllers

import com.google.inject.Inject
import email.{EmailMessageBuilder, EmailSellerMessageBuilder}
import models.AllCacheKeys
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.CompleteAndConfirmFormModel.Form.ConsentId
import models.CompleteAndConfirmResponseModel
import models.CompleteAndConfirmViewModel
import models.DateOfSaleFormModel
import models.K2KCacheKeyPrefix.CookiePrefix
import models.SellerEmailModel
import models.VehicleLookupFormModel
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import play.api.data.{FormError, Form}
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Call, Controller, Request, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.LogFormats.{anonymize, logMessage, optionNone}
import common.mappings.TitleType
import common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.services.{SEND, DateService}
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.common.{VssWebEndUserDto, VssWebHeaderDto}
import common.webserviceclients.acquire.{AcquireRequestDto, AcquireResponseDto, AcquireService, KeeperDetailsDto, TitleTypeDto}
import utils.helpers.Config
import views.html.changekeeper.complete_and_confirm
import webserviceclients.emailservice.EmailService

class CompleteAndConfirm @Inject()(webService: AcquireService, emailService: EmailService)
                                  (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                   dateService: DateService,
                                   config: Config) extends Controller {
  private val cookiesToBeDiscardedOnRedirectAway =
    AllCacheKeys ++ Set(AllowGoingToCompleteAndConfirmPageCacheKey)

  private[controllers] val form = Form(
    CompleteAndConfirmFormModel.Form.detailMapping
  )

  private final val NoCookiesFoundMessage = "Failed to find new keeper details and or vehicle details in cache" +
    "Now redirecting to vehicle lookup"

  def present = Action { implicit request =>
    canPerform {
      val result = for {
        newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
        vehicleAndKeeperDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
        dateOfSaleModel <- request.cookies.getModel[DateOfSaleFormModel]
      } yield Ok(
        complete_and_confirm(
          CompleteAndConfirmViewModel(
            form.fill(),
            vehicleAndKeeperDetails,
            newKeeperDetails,
            dateOfSaleModel
          ),
          dateService
        )
      )

      result getOrElse
        redirectToVehicleLookup(NoCookiesFoundMessage).discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey)

    }(Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardedOnRedirectAway))
  }

  def submit = Action.async { implicit request =>
    canPerform {
      form.bindFromRequest.fold(
        invalidForm => Future.successful {
          val result = for {
            newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
            vehicleDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
            dateOfSaleModel <- request.cookies.getModel[DateOfSaleFormModel]
          } yield {
            BadRequest(
              complete_and_confirm(
                CompleteAndConfirmViewModel(formWithReplacedErrors(invalidForm),
                  vehicleDetails,
                  newKeeperDetails,
                  dateOfSaleModel
                ),
                dateService
              )
            )
          }

          result getOrElse {
            Logger.warn(logMessage(s"Could not find expected data in cache on dispose submit - " +
              s"now redirecting to ${routes.VehicleLookup.present()}", request.cookies.trackingId()))
            Redirect(routes.VehicleLookup.present()).discardingCookies()
          }
        },
        validForm => processValidForm(validForm)
      )
    }(Future.successful(
    {
      Logger.warn(logMessage(s"Redirecting to ${routes.VehicleLookup.present()}", request.cookies.trackingId()))
      Redirect(routes.VehicleLookup.present()).discardingCookies(cookiesToBeDiscardedOnRedirectAway)
    }
    ))
  }

  private def processValidForm(validForm: CompleteAndConfirmFormModel)
                              (implicit request: Request[AnyContent]): Future[Result] = {
    val result = for {
      newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
      vehicleLookup <- request.cookies.getModel[VehicleLookupFormModel]
      vehicleDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
      sellerEmailModel <- request.cookies.getModel[SellerEmailModel]
      dateOfSaleModel <- request.cookies.getModel[DateOfSaleFormModel]
    } yield {
      Logger.debug(logMessage(s"CompleteAndConfirm - keeperEndDate = ${vehicleDetails.keeperEndDate}",
        request.cookies.trackingId()))
      Logger.debug(logMessage(s"CompleteAndConfirm - keeperChangeDate = ${vehicleDetails.keeperChangeDate}",
        request.cookies.trackingId()))
      // Only do the date check if the keeper end date or the keeper change date is present. If they are both
      // present or neither are present then skip the check

      acquireAction(
        validForm,
        newKeeperDetails,
        vehicleLookup,
        vehicleDetails,
        sellerEmailModel,
        dateOfSaleModel,
        request.cookies.trackingId
      )
    }

    result getOrElse Future.successful {
      Logger.warn(logMessage(s"Did not find expected cookie data on complete and confirm submit " +
        s"- now redirecting to ${routes.VehicleLookup.present()}", request.cookies.trackingId()))
      Redirect(routes.VehicleLookup.present()).discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey)
    }
  }

  private def redirectToVehicleLookup(message: String)(implicit request: Request[_]) = {
    Logger.warn(logMessage(message, request.cookies.trackingId()))
    Redirect(routes.VehicleLookup.present())
  }

  def back = Action { implicit request =>
    request.cookies.getModel[DateOfSaleFormModel].fold {
      request.cookies.getModel[NewKeeperEnterAddressManuallyFormModel] match {
        case Some(manualAddress) => {
          Logger.warn(logMessage(s"Redirecting to ${routes.NewKeeperEnterAddressManually.present()}",
            request.cookies.trackingId()))
          Redirect(routes.NewKeeperEnterAddressManually.present())
        }
        case None => {
          Logger.warn(logMessage(s"Redirecting to ${routes.NewKeeperChooseYourAddress.present()}",
            request.cookies.trackingId()))
          Redirect(routes.NewKeeperChooseYourAddress.present())
        }
      }
    } (dateOfSale => Redirect(routes.DateOfSale.present()))

  }

  private def formWithReplacedErrors(form: Form[CompleteAndConfirmFormModel]) =
    form.replaceError(
      ConsentId,
      "error.required",
      FormError(key = ConsentId, message = "change_keeper_keeperdetailscomplete.consentError", args = Seq.empty)
    ).distinctErrors

  private def acquireAction(completeAndConfirmForm: CompleteAndConfirmFormModel,
                            newKeeperDetailsView: NewKeeperDetailsViewModel,
                            vehicleLookup: VehicleLookupFormModel,
                            vehicleDetails: VehicleAndKeeperDetailsModel,
                            sellerEmailModel: SellerEmailModel,
                            dateOfSaleFormModel: DateOfSaleFormModel,
                            trackingId: String)
                           (implicit request: Request[AnyContent]): Future[Result] = {

    val transactionTimestamp = dateService.now.toDateTime

    val disposeRequest = buildMicroServiceRequest(vehicleLookup, completeAndConfirmForm,
      newKeeperDetailsView, dateOfSaleFormModel, transactionTimestamp, trackingId)

  logRequest(disposeRequest)

    webService.invoke(disposeRequest, trackingId).map {
      case (httpResponseCode, response) =>
        val result = Redirect(
          nextPage(httpResponseCode, response)(vehicleDetails, newKeeperDetailsView, sellerEmailModel,
            response.map(_.transactionId).getOrElse(""), transactionTimestamp, trackingId)
        ).withCookie(CompleteAndConfirmResponseModel(response.get.transactionId, transactionTimestamp))
         .withCookie(completeAndConfirmForm)

        result
    }.recover {
      case e: Throwable =>
        Logger.warn(logMessage(s"Acquire micro-service call failed.", request.cookies.trackingId()), e)
        Redirect(routes.MicroServiceError.present())
    }
  }.map(_.discardingCookie(AllowGoingToCompleteAndConfirmPageCacheKey))

  def nextPage(httpResponseCode: Int, response: Option[AcquireResponseDto])
              (vehicleDetails: VehicleAndKeeperDetailsModel,
               keeperDetails: NewKeeperDetailsViewModel,
               sellerEmailModel: SellerEmailModel,
               transactionId: String,
               transactionTimestamp: DateTime,
               trackingId: String)
              (implicit request: Request[_]) = {
    response.foreach(r => logResponse(r))

    response match {
      case Some(r) if r.responseCode.isDefined => successReturn(vehicleDetails, keeperDetails, sellerEmailModel,
        transactionId, transactionTimestamp, trackingId)
      case _ => handleHttpStatusCode(httpResponseCode)(vehicleDetails, keeperDetails, sellerEmailModel,
        transactionId, transactionTimestamp, trackingId)
    }
  }

  def buildMicroServiceRequest(vehicleLookup: VehicleLookupFormModel,
                               completeAndConfirmFormModel: CompleteAndConfirmFormModel,
                               newKeeperDetailsViewModel: NewKeeperDetailsViewModel,
                               dateOfSaleFormModel: DateOfSaleFormModel,
                               timestamp: DateTime,
                               trackingId: String): AcquireRequestDto = {

    val newKeeperDetails = buildKeeperDetails(newKeeperDetailsViewModel)

    val dateTimeFormatter = ISODateTimeFormat.dateTime()

    AcquireRequestDto(buildWebHeader(trackingId),
      vehicleLookup.referenceNumber,
      vehicleLookup.registrationNumber,
      newKeeperDetails,
      None,
      fleetNumber = newKeeperDetailsViewModel.fleetNumber,
      dateTimeFormatter.print(dateOfSaleFormModel.dateOfSale.toDateTimeAtStartOfDay(DateTimeZone.forID("UTC"))),
      dateOfSaleFormModel.mileage,
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
                           sellerEmailModel: SellerEmailModel,
                           transactionId: String,
                           transactionTimestamp: DateTime,
                           trackingId: String)
                          (implicit request: Request[_]): Call =
    statusCode match {
      case OK =>
        successReturn (vehicleDetails, keeperDetails, sellerEmailModel,  transactionId,
          transactionTimestamp, trackingId)
      case _ =>
        Logger.error(logMessage(s"Received http status code ${statusCode} from microservice call.  " +
          s"Redirecting to ${routes.MicroServiceError.present()}",
          request.cookies.trackingId()))
        routes.MicroServiceError.present()
    }

  private def successReturn(vehicleDetails: VehicleAndKeeperDetailsModel,
                            keeperDetails: NewKeeperDetailsViewModel,
                            sellerEmailModel: SellerEmailModel,
                            transactionId: String,
                            transactionTimestamp: DateTime,
                            trackingId: String
                            )
                           (implicit request: Request[_]): Call = {
    //send the email
    createAndSendSellerEmail(vehicleDetails, sellerEmailModel.email, transactionId,
      transactionTimestamp, trackingId)
    createAndSendEmail(vehicleDetails, keeperDetails, transactionId,
      transactionTimestamp, trackingId)
    //redirect
    Logger.debug(logMessage(s"Redirecting to ${routes.ChangeKeeperSuccess.present()}", request.cookies.trackingId()))
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

  private def logRequest(disposeRequest: AcquireRequestDto)(implicit request: Request[_]) = {
    Logger.debug(logMessage("Change keeper micro-service request",
      request.cookies.trackingId(),
      Seq(
        disposeRequest.webHeader.applicationCode,
        disposeRequest.webHeader.originDateTime.toString(),
        disposeRequest.webHeader.serviceTypeCode,
        disposeRequest.webHeader.transactionId,
        disposeRequest.dateOfTransfer,
        disposeRequest.fleetNumber.getOrElse(optionNone),
        disposeRequest.keeperConsent.toString,
        disposeRequest.mileage.toString,
        anonymize(disposeRequest.referenceNumber),
        anonymize(disposeRequest.registrationNumber),
        disposeRequest.requiresSorn.toString,
        disposeRequest.transactionTimestamp
    )))
  }

  private def logResponse(disposeResponse: AcquireResponseDto)(implicit request: Request[_]) = {
    Logger.debug(logMessage("Change keeper micro-service request",
      request.cookies.trackingId(),
      Seq(anonymize(disposeResponse.registrationNumber),
        disposeResponse.responseCode.getOrElse(""),
        anonymize(disposeResponse.transactionId)))
    )
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
      Logger.warn(logMessage(s"Could not find AllowGoingToCompleteAndConfirmPageCacheKey in the request. " +
        s"Redirect to starting page discarding cookies", request.cookies.trackingId()))
      redirect
    }(c => action)
  }

  /**
   * Calling this method on a successful submission, will send an email if we have the new keeper details.
   * @param keeperDetails the keeper model from the cookie.
   * @return
   */
  def createAndSendEmail(vehicleDetails: VehicleAndKeeperDetailsModel,
                         keeperDetails: NewKeeperDetailsViewModel,
                         transactionId: String,
                         transactionTimestamp: DateTime,
                         trackingId: String)
                        (implicit request: Request[_]) =
    keeperDetails.email match {
      case Some(emailAddr) =>
        import scala.language.postfixOps

        import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

        implicit val emailConfiguration = config.emailConfiguration
        implicit val implicitEmailService = implicitly[EmailService](emailService)

        val template = EmailMessageBuilder.buildWith(vehicleDetails, transactionId, transactionTimestamp)

        // This sends the email.
        SEND email template withSubject s"${vehicleDetails.registrationNumber} Confirmation of new vehicle keeper" to emailAddr send trackingId

      case None => Logger.info(logMessage(s"tried to send an email with no keeper details",
        request.cookies.trackingId()))
    }

  def createAndSendSellerEmail(vehicleDetails: VehicleAndKeeperDetailsModel,
                               sellerEmail: Option[String],
                               transactionId: String,
                               transactionTimestamp: DateTime,
                               trackingId: String)
                              (implicit request: Request[_])=
    sellerEmail match {
      case Some(emailAddr) =>
        import scala.language.postfixOps

        import SEND._ // Keep this local so that we don't pollute rest of the class with unnecessary imports.

        implicit val emailConfiguration = config.emailConfiguration
        implicit val implicitEmailService = implicitly[EmailService](emailService)

        val template = EmailSellerMessageBuilder.buildWith(vehicleDetails, transactionId, transactionTimestamp)

        // This sends the email.
        SEND email template withSubject s"${vehicleDetails.registrationNumber} confirmation of vehicle keeper change" to emailAddr send trackingId
      case None => Logger.info(logMessage(s"tried to send a receipt to seller but no email was found",
        request.cookies.trackingId()))
    }
}
