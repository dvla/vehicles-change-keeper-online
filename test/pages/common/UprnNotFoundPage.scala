package pages.common

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import org.openqa.selenium.WebDriver
import views.common.UprnNotFound.{VehicleLookupId, ManualaddressbuttonId}
import pages.changekeeper.buildAppUrl

object UprnNotFoundPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("uprn-not-found")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Error confirming post code"

  def setupTradeDetails(implicit driver: WebDriver): Element = find(id(VehicleLookupId)).get

  def manualAddress(implicit driver: WebDriver): Element = find(id(ManualaddressbuttonId)).get
}
