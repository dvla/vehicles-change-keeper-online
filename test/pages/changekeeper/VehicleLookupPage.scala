package pages.changekeeper

import models.VehicleLookupFormModel.Form.DocumentReferenceNumberId
import models.VehicleLookupFormModel.Form.VehicleRegistrationNumberId
import models.VehicleLookupFormModel.Form.VehicleSellerEmailOption
import models.VehicleLookupFormModel.Form.VehicleSoldToId
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element}
import uk.gov.dvla.vehicles.presentation.common
import common.mappings.OptionalToggle.{Visible, Invisible}
import common.helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.VehicleLookup.{VehicleSoldTo_Private, VehicleSoldTo_Business, BackId, SubmitId}
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{ReferenceNumberValid, RegistrationNumberValid}

object VehicleLookupPage extends Page {
  private final val route = "vehicle-lookup"
  final val address = buildAppUrl(route)

  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  val cegUrl: String = WebDriverFactory.testUrl + buildAppUrl(s"${route}/ceg").substring(1)

  final override val title: String = "Details of the vehicle being sold"

  def vehicleRegistrationNumber(implicit driver: WebDriver): TextField = textField(id(VehicleRegistrationNumberId))

  def documentReferenceNumber(implicit driver: WebDriver): TelField = telField(id(DocumentReferenceNumberId))

  def vehicleSoldToPrivateIndividual(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${VehicleSoldToId}_$VehicleSoldTo_Private"))

  def vehicleSoldToBusiness(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${VehicleSoldToId}_$VehicleSoldTo_Business"))

  def emailVisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${VehicleSellerEmailOption}_$Visible"))

  def emailInvisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${VehicleSellerEmailOption}_$Invisible"))

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def happyPath(referenceNumber: String = ReferenceNumberValid,
                registrationNumber: String = RegistrationNumberValid,
                isVehicleSoldToPrivateIndividual: Boolean = true)
               (implicit driver: WebDriver) = {
    go to VehicleLookupPage

    documentReferenceNumber.value = referenceNumber
    VehicleLookupPage.vehicleRegistrationNumber.value = registrationNumber
    click on emailInvisible
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    if (isVehicleSoldToPrivateIndividual) click on vehicleSoldToPrivateIndividual
    else click on vehicleSoldToBusiness
    click on next
  }
}
