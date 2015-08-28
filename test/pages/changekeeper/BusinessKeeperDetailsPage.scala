package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.{RadioButton, WebDriverFactory, WebBrowserDSL, Page, TelField, TextField, Element}
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.mappings.OptionalToggle.{Visible, Invisible}
import common.model.BusinessKeeperDetailsFormModel.Form.BusinessNameId
import common.model.BusinessKeeperDetailsFormModel.Form.EmailId
import common.model.BusinessKeeperDetailsFormModel.Form.EmailOptionId
import common.model.BusinessKeeperDetailsFormModel.Form.FleetNumberId
import common.model.BusinessKeeperDetailsFormModel.Form.FleetNumberOptionId
import common.model.BusinessKeeperDetailsFormModel.Form.PostcodeId
import views.changekeeper.BusinessKeeperDetails.{BackId, NextId}

object BusinessKeeperDetailsPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("business-keeper-details")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Enter new keeper details"

  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  def fleetNumberVisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${FleetNumberOptionId}_$Visible"))

  def fleetNumberInvisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${FleetNumberOptionId}_$Invisible"))

  def fleetNumberField(implicit driver: WebDriver): TelField = telField(id(FleetNumberId))

  def businessNameField(implicit driver: WebDriver): TextField = textField(id(BusinessNameId))

  def emailVisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Visible"))

  def emailInvisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Invisible"))

  def emailField(implicit driver: WebDriver): TextField = textField(id(s"${EmailId}_$EmailEnterId"))

  def emailConfirmField(implicit driver: WebDriver): TextField = textField(id(s"${EmailId}_$EmailVerifyId"))

  def postcodeField(implicit driver: WebDriver): TextField = textField(id(PostcodeId))

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(NextId)).get

  def navigate(fleetNumber: String = FleetNumberValid,
               businessName: String = BusinessNameValid,
               email: String = EmailValid,
               postcode: String = PostcodeValid)(implicit driver: WebDriver) = {
    go to BusinessKeeperDetailsPage

    click on fleetNumberVisible
    fleetNumberField enter fleetNumber
    businessNameField enter businessName
    click on emailVisible
    emailField enter email
    emailConfirmField enter email
    postcodeField enter postcode

    click on next
  }

  def submitPostcodeWithoutAddresses(implicit driver: WebDriver) = {
    navigate(postcode = PostcodeInvalid)
  }
}
