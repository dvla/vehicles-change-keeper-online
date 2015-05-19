package controllers

import javax.inject.Inject

import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common.LogFormats.logMessage
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperChooseYourAddressViewModel
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.webserviceclients.addresslookup.AddressLookupService
import uk.gov.dvla.vehicles.presentation.common.controllers.NewKeeperChooseYourAddressBase
import utils.helpers.Config
import views.html.changekeeper.new_keeper_choose_your_address
import models.K2KCacheKeyPrefix.CookiePrefix

class NewKeeperChooseYourAddress @Inject()(protected override val addressLookupService: AddressLookupService)
                                          (implicit protected override val clientSideSessionFactory: ClientSideSessionFactory,
                                           config: Config) extends NewKeeperChooseYourAddressBase(addressLookupService) {

  override protected def ordnanceSurveyUseUprn: Boolean = config.ordnanceSurveyUseUprn

  override protected def invalidFormResult(model: NewKeeperChooseYourAddressViewModel,
                                  name: String,
                                  postcode: String,
                                  email: Option[String],
                                  addresses: Seq[(String, String)],
                                  isBusinessKeeper: Boolean,
                                  fleetNumber: Option[String])(implicit request: Request[_]): Result = {
    BadRequest(new_keeper_choose_your_address(
      model,
      name,
      postcode,
      email,
      addresses,
      isBusinessKeeper,
      fleetNumber
    ))
  }

  override protected def presentView(model: NewKeeperChooseYourAddressViewModel,
                            name: String,
                            postcode: String,
                            email: Option[String],
                            addresses: Seq[(String, String)],
                            isBusinessKeeper: Boolean,
                            fleetNumber: Option[String])(implicit request: Request[_]): Result = {
    
    Ok(views.html.changekeeper.new_keeper_choose_your_address(
      model, name, postcode, email, addresses, isBusinessKeeper, fleetNumber)
    )
  }

  override protected def privateKeeperDetailsRedirect(implicit request: Request[_]) = {
    Logger.debug(logMessage(s"Redirecting to ${routes.PrivateKeeperDetails.present()}", request.cookies.trackingId()))
    Redirect(routes.PrivateKeeperDetails.present())
  }

  override protected def businessKeeperDetailsRedirect(implicit request: Request[_]) = {
    Logger.debug(logMessage(s"Redirecting to ${routes.BusinessKeeperDetails.present()}", request.cookies.trackingId()))
    Redirect(routes.BusinessKeeperDetails.present())
  }
  
  override protected def vehicleLookupRedirect(implicit request: Request[_]) = {
    Logger.debug(logMessage(s"Redirecting to ${routes.VehicleLookup.present()}", request.cookies.trackingId()))
    Redirect(routes.VehicleLookup.present())
  }
  
  override protected def completeAndConfirmRedirect(implicit request: Request[_]) = {
    Logger.debug(logMessage(s"Redirecting to ${routes.DateOfSale.present()}", request.cookies.trackingId()))
    Redirect(routes.DateOfSale.present())
  }
  
  override protected def upnpNotFoundRedirect(implicit request: Request[_]) = {
    Logger.debug(logMessage(s"Redirecting to ${routes.UprnNotFound.present()}", request.cookies.trackingId()))
    Redirect(routes.UprnNotFound.present())
  }

}
