package controllers

import com.google.inject.Inject
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.DateOfSaleFormModel
import models.DateOfSaleFormModel.Form.MileageId
import models.DateOfSaleViewModel
import models.K2KCacheKeyPrefix.CookiePrefix
import models.SellerEmailModel
import models.VehicleLookupFormModel
import org.joda.time.{DateTime, LocalDate}
import play.api.data.{FormError, Form}
import play.api.mvc.{Action, AnyContent, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.LogFormats.DVLALogger
import common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.services.DateService
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.acquire.AcquireService
import common.webserviceclients.emailservice.EmailService
import utils.helpers.Config
import views.html.changekeeper.date_of_sale

object DateOfSaleCheck extends Enumeration {
  val DateOfDisposalAfterDateOfSale, DateOfSaleOver12Months, Ok = Value
}

class DateOfSale @Inject()(webService: AcquireService, emailService: EmailService)
                                  (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                   dateService: DateService,
                                   config: Config) extends Controller with DVLALogger {

  private[controllers] val form = Form(
    DateOfSaleFormModel.Form.detailMapping
  )

  private final val NoCookiesFoundMessage = "Failed to find new keeper details and or vehicle details in cache" +
    "Now redirecting to vehicle lookup"

  def present = Action { implicit request =>
      val result = for {
        newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
        vehicleAndKeeperDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
      } yield {
          logMessage(request.cookies.trackingId(), Info, "Presenting date of sale view")
          Ok(
            date_of_sale(
              DateOfSaleViewModel(
                form.fill(),
                vehicleAndKeeperDetails,
                newKeeperDetails,
                showDateOfSaleWarning= false
              ),
              dateService
            )
          )
        }
      result getOrElse redirectToVehicleLookup(NoCookiesFoundMessage, request.cookies.trackingId())
  }

  // The dates are valid if they are on the same date or if the disposal date/keeper change date
  // is before the date of sale
  def submitWithDateCheck = submitBase({
    case (Some(keeperEndOrChangeDate), dateOfSale) if keeperEndOrChangeDate.toLocalDate.isAfter(dateOfSale) =>
      DateOfSaleCheck.DateOfDisposalAfterDateOfSale
    case (_, dateOfSale) if dateOfSale.isBefore(dateService.now.toDateTime.toLocalDate.minusMonths(12)) =>
      DateOfSaleCheck.DateOfSaleOver12Months
    case _ =>
      DateOfSaleCheck.Ok
  })

  def submitNoDateCheck = submitBase((_, _) => DateOfSaleCheck.Ok)

  private def submitBase(validDates: (Option[DateTime], LocalDate) => DateOfSaleCheck.Value) = Action { implicit request =>
    form.bindFromRequest.fold(
      invalidForm => {
        val result = for {
          newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
          vehicleDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
        } yield BadRequest(
            date_of_sale(
              DateOfSaleViewModel(
                formWithReplacedErrors(invalidForm),
                vehicleDetails,
                newKeeperDetails,
                showDateOfSaleWarning = false
              ),
            dateService
          )
        )
        result getOrElse {
          logMessage(request.cookies.trackingId(),
            Warn,
            "Could not find expected data in cache on dispose submit - now redirecting..."
          )
          Redirect(routes.VehicleLookup.present()).discardingCookies()
        }
      },
      validForm => processValidForm(validDates, validForm)
    )
  }

  private def processValidForm(validDates: (Option[DateTime], LocalDate) => DateOfSaleCheck.Value, validModel: DateOfSaleFormModel)
                              (implicit request: Request[AnyContent]): Result = {
    val result = for {
      newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
      vehicleLookup <- request.cookies.getModel[VehicleLookupFormModel]
      vehicleDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
      sellerEmailModel <- request.cookies.getModel[SellerEmailModel]
    } yield {
      logMessage(request.cookies.trackingId(),
        Debug,
        s"CompleteAndConfirm - keeperEndDate = ${vehicleDetails.keeperEndDate}"
      )
      logMessage(request.cookies.trackingId(),
        Debug,
        s"CompleteAndConfirm - keeperChangeDate = ${vehicleDetails.keeperChangeDate}"
      )
      // Only do the date check if the keeper end date or the keeper change date is present. If they are both
      // present or neither are present then skip the check

      def dateInvalidCall(warningModel: Form[DateOfSaleFormModel], disposalDate: Option[DateTime]) = {
        BadRequest(date_of_sale(
          DateOfSaleViewModel(
            warningModel,
            vehicleDetails,
            newKeeperDetails,
            showDateOfSaleWarning = true,
            // Next time the submit will not perform any date check
            submitAction = controllers.routes.DateOfSale.submitNoDateCheck(),
            // Pass the dateOfDisposal/change date so we can tell the user in the warning
            disposalDate = disposalDate.map(_.toString("dd/MM/yyyy"))
          ),
          dateService))
      }

      def dateCheck(keeperChangeOrEndDate: Option[DateTime]) = {
        validDates(keeperChangeOrEndDate, validModel.dateOfSale) match {
          case DateOfSaleCheck.Ok =>
            Redirect(routes.CompleteAndConfirm.present())
              .withCookie(validModel)
              .withCookie(AllowGoingToCompleteAndConfirmPageCacheKey, "true")
          case DateOfSaleCheck.DateOfDisposalAfterDateOfSale =>
            logMessage(request.cookies.trackingId(), Debug, "Date of sale date validation failed: disposalDate " +
              s"($keeperChangeOrEndDate) after dateOfSale (${validModel.dateOfSale})")
            // VMPRCI-4450: Don't pass through the entered date of sale and ignore the resulting error
            val model = form.bind(Map(
              models.DateOfSaleFormModel.Form.MileageId -> validModel.mileage.fold("")(_.toString)
            )).discardingErrors

            dateInvalidCall(model, keeperChangeOrEndDate)
          case DateOfSaleCheck.DateOfSaleOver12Months =>
            logMessage(request.cookies.trackingId(), Debug, "Date of sale date validation failed: dateOfSale " +
              s"(${validModel.dateOfSale}) over 12 months")
            dateInvalidCall(form.fill(validModel), None)
        }
      }

      (vehicleDetails.keeperChangeDate, vehicleDetails.keeperEndDate) match {
        case (Some(keeperChangeDate), None) => dateCheck(vehicleDetails.keeperChangeDate)
        case (None, Some(keeperEndDate)) => dateCheck(vehicleDetails.keeperEndDate)
        // Either both dates are missing or they are both populated so just call the acquire service
        // and move to the next page
        case _ => dateCheck(None)
      }
    }

    result getOrElse {
      logMessage(
        request.cookies.trackingId(),
        Warn,
        "Did not find expected cookie data on complete and confirm submit - now redirecting to VehicleLookup..."
      )
      Redirect(routes.VehicleLookup.present())
    }
  }

  private def redirectToVehicleLookup(message: String, trackingId: TrackingId) = {
    logMessage(trackingId, Warn, message)
    Redirect(routes.VehicleLookup.present())
  }

  def back = Action { implicit request =>
    request.cookies.getModel[NewKeeperEnterAddressManuallyFormModel] match {
      case Some(manualAddress) =>
        Redirect(routes.NewKeeperEnterAddressManually.present())
      case None => Redirect(routes.NewKeeperChooseYourAddress.present())
    }
  }

  private def formWithReplacedErrors(form: Form[DateOfSaleFormModel]) =
    form.replaceError(
        MileageId,
        "error.number",
        FormError(
          key = MileageId,
          message = "change_keeper_privatekeeperdetailscomplete.mileage.validation",
          args = Seq.empty
        )
    ).distinctErrors
}
