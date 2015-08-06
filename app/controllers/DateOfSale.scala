package controllers

import com.google.inject.Inject
import models.CompleteAndConfirmFormModel._
import models.DateOfSaleFormModel._
import models.DateOfSaleFormModel.Form.MileageId
import models._
import models.K2KCacheKeyPrefix.CookiePrefix
import org.joda.time.{DateTime, LocalDate}
import play.api.data.{FormError, Form}
import play.api.mvc.{Action, AnyContent, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common.LogFormats.DVLALogger
import webserviceclients.emailservice.EmailService
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.model.{NewKeeperDetailsViewModel, VehicleAndKeeperDetailsModel}
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.services.DateService
import common.views.helpers.FormExtensions.formBinding
import common.webserviceclients.acquire.AcquireService
import utils.helpers.Config
import views.html.changekeeper.date_of_sale

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
          Ok(
            date_of_sale(
              DateOfSaleViewModel(
                form.fill(),
                vehicleAndKeeperDetails,
                newKeeperDetails,
                isSaleDateInvalid = false,
                isDateToCompareDisposalDate = false
              ),
              dateService
            )
          )
        }
      result getOrElse redirectToVehicleLookup(NoCookiesFoundMessage, request.cookies.trackingId())
  }

  // The dates are valid if they are on the same date or if the disposal date/keeper change date is before the acquisition date
  def submitWithDateCheck = submitBase(
    (keeperEndDateOrChangeDate, dateOfSale) =>
      keeperEndDateOrChangeDate.toLocalDate.isEqual(dateOfSale) || keeperEndDateOrChangeDate.toLocalDate.isBefore(dateOfSale)
  )

  def submitNoDateCheck = submitBase((keeperEndDateOrChangeDate, dateOfSale) => true)

  private def submitBase(validDates: (DateTime, LocalDate) => Boolean) = Action { implicit request =>
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
                isSaleDateInvalid = false,
                isDateToCompareDisposalDate = false
              ),
            dateService
          )
        )
        result getOrElse {
          logMessage(request.cookies.trackingId(), Warn, "Could not find expected data in cache on dispose submit - now redirecting...")
          Redirect(routes.VehicleLookup.present()).discardingCookies()
        }
      } ,
      validForm => processValidForm(validDates, validForm)
    )
  }

  private def processValidForm(validDates: (DateTime, LocalDate) => Boolean, validModel: DateOfSaleFormModel)
                              (implicit request: Request[AnyContent]): Result = {
    val result = for {
      newKeeperDetails <- request.cookies.getModel[NewKeeperDetailsViewModel]
      vehicleLookup <- request.cookies.getModel[VehicleLookupFormModel]
      vehicleDetails <- request.cookies.getModel[VehicleAndKeeperDetailsModel]
      sellerEmailModel <- request.cookies.getModel[SellerEmailModel]
    } yield {
      logMessage(request.cookies.trackingId(), Debug, s"CompleteAndConfirm - keeperEndDate = ${vehicleDetails.keeperEndDate}")
      logMessage(request.cookies.trackingId(), Debug, s"CompleteAndConfirm - keeperChangeDate = ${vehicleDetails.keeperChangeDate}")
      // Only do the date check if the keeper end date or the keeper change date is present. If they are both
      // present or neither are present then skip the check

      Seq(vehicleDetails.keeperChangeDate, vehicleDetails.keeperEndDate).flatten match {
        case Seq(endDateOrChangeDate) if validDates(endDateOrChangeDate, validModel.dateOfSale) =>
          // Either the keeper end date or the keeper change date is populated so do the date check
          // The dateOfSale is valid
          Redirect(routes.CompleteAndConfirm.present())
            .withCookie(validModel)
            .withCookie(AllowGoingToCompleteAndConfirmPageCacheKey, "true")
        case Seq(endDateOrChangeDate) =>
          // The dateOfSale is invalid
          BadRequest(date_of_sale(
            DateOfSaleViewModel(
              form.fill(validModel),
              vehicleDetails,
              newKeeperDetails,
              isSaleDateInvalid = true, // This will tell the page to display the date warning
              isDateToCompareDisposalDate = vehicleDetails.keeperEndDate.isDefined,
              submitAction = controllers.routes.DateOfSale.submitNoDateCheck(), // Next time the submit will not perform any date check
              dateToCompare = Some(endDateOrChangeDate.toString("dd/MM/yyyy")) // Pass the dateOfDisposal/change date so we can tell the user in the warning
            ),
            dateService)
          )
        case _ =>
          // Either both dates are missing or they are both populated so just call the acquire service
          // and move to the next page
          Redirect(routes.CompleteAndConfirm.present())
            .withCookie(validModel)
            .withCookie(AllowGoingToCompleteAndConfirmPageCacheKey, "true")
      }
    }

    result getOrElse {
      logMessage(request.cookies.trackingId(), Warn, "Did not find expected cookie data on complete and confirm submit - now redirecting to VehicleLookup...")
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
        FormError(key = MileageId, message = "change_keeper_privatekeeperdetailscomplete.mileage.validation", args = Seq.empty)
    ).distinctErrors
}
