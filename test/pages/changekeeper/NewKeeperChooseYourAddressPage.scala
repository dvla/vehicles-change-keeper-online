package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.{Page, WebDriverFactory}
import common.model.NewKeeperChooseYourAddressFormModel.Form.AddressSelectId
import views.changekeeper.NewKeeperChooseYourAddress
import NewKeeperChooseYourAddress.BackId
import NewKeeperChooseYourAddress.EnterAddressManuallyButtonId
import NewKeeperChooseYourAddress.SelectId
import org.scalatest.selenium.WebBrowser.{SingleSel, singleSel, tagName, click, go, find, id, Element}

object NewKeeperChooseYourAddressPage extends Page {
  final val address = buildAppUrl("new-keeper-choose-your-address")
  final val selectedAddress = "presentationProperty stub, 123, property stub, street stub, town stub, area stub, QQ99QQ"
  final val defaultSelectedAddress = "Not real street 1, Not real street2, Not real town, QQ9 9QQ"
  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title = "Select new keeper address"

  def chooseAddress(implicit driver: WebDriver): SingleSel = singleSel(id(AddressSelectId))

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SelectId)).get

  def manualAddress(implicit driver: WebDriver): Element = find(id(EnterAddressManuallyButtonId)).get

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

  def happyPath(implicit driver: WebDriver) = {
    go to NewKeeperChooseYourAddressPage
    chooseAddress.value = selectedAddress
    click on next
  }

  def sadPath(implicit driver: WebDriver) = {
    go to NewKeeperChooseYourAddressPage
    click on next
  }
}
