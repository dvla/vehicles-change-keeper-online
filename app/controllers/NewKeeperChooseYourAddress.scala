package controllers

import javax.inject.Inject
import models._
import play.api.Logger
import play.api.data.{Form, FormError}
import play.api.mvc.{AnyContent, Action, Controller, Request, Result}
import uk.gov.dvla.vehicles.presentation.common.model.{VehicleAndKeeperDetailsModel, AddressModel, VehicleDetailsModel}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieImplicits.{RichCookies, RichForm, RichResult}
import common.clientsidesession.ClientSideSessionFactory
import common.webserviceclients.addresslookup.AddressLookupService
import common.views.helpers.FormExtensions.formBinding
import utils.helpers.Config
import views.html.changekeeper.new_keeper_choose_your_address
import scala.Some
import play.api.mvc.Result
import models.NewKeeperDetailsViewModel.getTitle

class NewKeeperChooseYourAddress @Inject()(addressLookupService: AddressLookupService)
                                          (implicit clientSideSessionFactory: ClientSideSessionFactory,
                                           config: Config) extends Controller {

  private[controllers] val form = Form(NewKeeperChooseYourAddressFormModel.Form.Mapping)

  private final val KeeperDetailsNotInCacheMessage = "Failed to find keeper details in cache. " +
    "Now redirecting to vehicle lookup."
  private final val PrivateAndBusinessKeeperDetailsBothInCacheMessage = "Both private and business keeper details " +
    "found in cache. This is an error condition. Now redirecting to vehicle lookup."
  private final val VehicleDetailsNotInCacheMessage = "Failed to find vehicle details in cache. " +
    "Now redirecting to vehicle lookup"

  private def switch[R](onPrivate: PrivateKeeperDetailsFormModel => R,
                        onBusiness: BusinessKeeperDetailsFormModel => R,
                        onError: String => R)
                       (implicit request: Request[AnyContent]): R = {
    val privateKeeperDetailsOpt = request.cookies.getModel[PrivateKeeperDetailsFormModel]
    val businessKeeperDetailsOpt = request.cookies.getModel[BusinessKeeperDetailsFormModel]
    (privateKeeperDetailsOpt, businessKeeperDetailsOpt) match {
      case (Some(privateKeeperDetails), Some(businessKeeperDetails)) => onError(PrivateAndBusinessKeeperDetailsBothInCacheMessage)
      case (Some(privateKeeperDetails), _) => onPrivate(privateKeeperDetails)
      case (_, Some(businessKeeperDetails)) => onBusiness(businessKeeperDetails)
      case _ => onError(KeeperDetailsNotInCacheMessage)
    }
  }

  def present = Action.async {
    implicit request =>
      switch(
        privateKeeperDetails => fetchAddresses(privateKeeperDetails.postcode).map {
          addresses =>
            if (config.ordnanceSurveyUseUprn) {
              openView(
                buildKeeperName(privateKeeperDetails), privateKeeperDetails.postcode, privateKeeperDetails.email, addresses
              )
            } else {
              openView(
                buildKeeperName(privateKeeperDetails), privateKeeperDetails.postcode, privateKeeperDetails.email, index(addresses)
              )
            }
          },
        businessKeeperDetails => fetchAddresses(businessKeeperDetails.postcode).map {
          addresses =>
            if (config.ordnanceSurveyUseUprn) {
              openView(
                businessKeeperDetails.businessName, businessKeeperDetails.postcode, businessKeeperDetails.email, addresses
              )
            } else {
              openView(
                businessKeeperDetails.businessName, businessKeeperDetails.postcode, businessKeeperDetails.email, index(addresses)
              )
            }
          },
        message => Future.successful(error(message))
      )
  }

  private def error(message: String): Result = {
    Logger.warn(message)
    Redirect(routes.VehicleLookup.present())
  }

  private def buildKeeperName(privateKeeperDetails: PrivateKeeperDetailsFormModel): String =
    s"${getTitle(privateKeeperDetails.title)} ${privateKeeperDetails.firstName} ${privateKeeperDetails.lastName}"

  private def fetchAddresses(postcode: String)(implicit request: Request[_]) = {
    val session = clientSideSessionFactory.getSession(request.cookies)
    addressLookupService.fetchAddressesForPostcode(postcode, session.trackingId)
  }

  private def openView(name: String, postcode: String, email: Option[String], addresses: Seq[(String, String)])
                      (implicit request: Request[_]) =
    request.cookies.getModel[VehicleAndKeeperDetailsModel] match {
      case Some(vehicleAndKeeperDetails) =>
        Ok(views.html.changekeeper.new_keeper_choose_your_address(
          NewKeeperChooseYourAddressViewModel(form.fill(), vehicleAndKeeperDetails), name, postcode, email, addresses)
        )
      case _ => error(VehicleDetailsNotInCacheMessage)
    }

  private def index(addresses: Seq[(String, String)]) =
    addresses.map {
      case (uprn, address) => address
    }. // Extract the address.
      zipWithIndex. // Add an index for each address
      map {
      case (address, index) => (index.toString, address)
    } // Flip them around so index comes first.

  def submit = Action.async { implicit request => Future(Ok("success")) }

}