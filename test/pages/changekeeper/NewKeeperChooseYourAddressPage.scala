package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.{Element, Page, SingleSel, WebBrowserDSL, WebDriverFactory}
import common.model.NewKeeperChooseYourAddressFormModel.Form.AddressSelectId
import views.changekeeper.NewKeeperChooseYourAddress
import NewKeeperChooseYourAddress.BackId
import NewKeeperChooseYourAddress.EnterAddressManuallyButtonId
import NewKeeperChooseYourAddress.SelectId

object NewKeeperChooseYourAddressPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("new-keeper-choose-your-address")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title = "Select new keeper address"

  def chooseAddress(implicit driver: WebDriver): SingleSel = singleSel(id(AddressSelectId))

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SelectId)).get

  def manualAddress(implicit driver: WebDriver): Element = find(id(EnterAddressManuallyButtonId)).get

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

  def getList(implicit driver: WebDriver) = singleSel(id(AddressSelectId)).getOptions

  def getListCount(implicit driver: WebDriver): Int = getList.size

  def select(implicit driver: WebDriver): Element = find(id(SelectId)).get

  def happyPath(implicit driver: WebDriver) = {
    go to NewKeeperChooseYourAddressPage
    chooseAddress.value = "0"
    click on select
  }

  def sadPath(implicit driver: WebDriver) = {
    go to NewKeeperChooseYourAddressPage
    click on select
  }
}
