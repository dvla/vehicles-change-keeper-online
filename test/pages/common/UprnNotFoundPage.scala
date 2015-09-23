package pages.common

import org.openqa.selenium.WebDriver
import pages.changekeeper.buildAppUrl
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import views.common.UprnNotFound.{VehicleLookupId, ManualaddressbuttonId}
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element}

object UprnNotFoundPage extends Page {
  final val address = buildAppUrl("uprn-not-found")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Error confirming post code"

  def setupTradeDetails(implicit driver: WebDriver): Element = find(id(VehicleLookupId)).get

  def manualAddress(implicit driver: WebDriver): Element = find(id(ManualaddressbuttonId)).get
}
