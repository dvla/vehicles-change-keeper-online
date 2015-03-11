package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser._
import uk.gov.dvla.vehicles.presentation.common
import common.model.BusinessKeeperDetailsFormModel.Form.{BusinessNameId, EmailId, FleetNumberId, PostcodeId}
import uk.gov.dvla.vehicles.presentation.common.mappings.OptionalToggle._
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsFormModel.Form.EmailOptionId
import views.changekeeper.BusinessKeeperDetails.{BackId, NextId}
import org.openqa.selenium.WebDriver

object BusinessKeeperDetailsPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("business-keeper-details")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Enter new keeper details"

  final val FleetNumberValid = "123456"
  final val BusinessNameValid = "Brand New Motors"
  final val EmailValid = "my@email.com"
  final val PostcodeValid = "QQ99QQ"
  final val PostcodeInvalid = "XX99XX"

  def fleetNumberField(implicit driver: WebDriver): TelField = telField(id(FleetNumberId))

  def businessNameField(implicit driver: WebDriver): TextField = textField(id(BusinessNameId))

  def emailVisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Visible"))

  def emailInvisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Invisible"))

  def emailField(implicit driver: WebDriver): TextField = textField(id(EmailId))

  def postcodeField(implicit driver: WebDriver): TextField = textField(id(PostcodeId))

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(NextId)).get

  def errorTextInBusinessKeeperPage(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

  def navigate(fleetNumber: String = FleetNumberValid,
               businessName: String = BusinessNameValid,
               email: String = EmailValid,
               postcode: String = PostcodeValid)(implicit driver: WebDriver) = {
    go to BusinessKeeperDetailsPage

    fleetNumberField enter fleetNumber
    businessNameField enter businessName
    click on emailVisible
    emailField enter email
    postcodeField enter postcode

    click on next
  }

  def submitPostcodeWithoutAddresses(implicit driver: WebDriver) = {
    navigate(postcode = PostcodeInvalid)
  }
}
