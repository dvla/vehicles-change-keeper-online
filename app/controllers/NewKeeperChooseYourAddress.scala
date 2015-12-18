package controllers

import javax.inject.Inject
import models.K2KCacheKeyPrefix.CookiePrefix
import play.api.mvc.{Request, Result}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.CookieImplicits.RichCookies
import common.controllers.NewKeeperChooseYourAddressBase
import common.model.NewKeeperChooseYourAddressViewModel
import common.webserviceclients.addresslookup.AddressLookupService
import utils.helpers.Config
import views.html.changekeeper.new_keeper_choose_your_address
import common.views.constraints.Postcode.formatPostcode

class NewKeeperChooseYourAddress @Inject()(protected override val addressLookupService: AddressLookupService)
                                          (implicit protected override val clientSideSessionFactory: ClientSideSessionFactory,
                                           config: Config) extends NewKeeperChooseYourAddressBase(addressLookupService) {

  override protected def invalidFormResult(model: NewKeeperChooseYourAddressViewModel,
                                  name: String,
                                  postcode: String,
                                  email: Option[String],
                                  addresses: Seq[(String, String)],
                                  isBusinessKeeper: Boolean,
                                  fleetNumber: Option[String])(implicit request: Request[_]): Result =
    BadRequest(new_keeper_choose_your_address(
      model,
      name,
      postcode,
      email,
      addresses,
      isBusinessKeeper,
      fleetNumber
      )
    )

  override protected def presentView(model: NewKeeperChooseYourAddressViewModel,
                            name: String,
                            postcode: String,
                            email: Option[String],
                            addresses: Seq[(String, String)],
                            isBusinessKeeper: Boolean,
                            fleetNumber: Option[String])(implicit request: Request[_]): Result = {
    logMessage(request.cookies.trackingId(), Info, "Presenting new keeper choose your address view")
    Ok(views.html.changekeeper.new_keeper_choose_your_address(
      model, name, formatPostcode(postcode), email, addresses, isBusinessKeeper, fleetNumber)
    )
  }

  override protected def privateKeeperDetailsRedirect(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.PrivateKeeperDetails.present()}")
    Redirect(routes.PrivateKeeperDetails.present())
  }

  override protected def businessKeeperDetailsRedirect(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.BusinessKeeperDetails.present()}")
    Redirect(routes.BusinessKeeperDetails.present())
  }
  
  override protected def vehicleLookupRedirect(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.VehicleLookup.present()}")
    Redirect(routes.VehicleLookup.present())
  }
  
  override protected def completeAndConfirmRedirect(implicit request: Request[_]) = {
    logMessage(request.cookies.trackingId(), Debug, s"Redirecting to ${routes.DateOfSale.present()}")
    Redirect(routes.DateOfSale.present())
  }
}
