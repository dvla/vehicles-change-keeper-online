package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.VrmLocked
import VrmLocked.{ExitId, BuyAnotherVehicleId}
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element, tagName}

object VrmLockedPage extends Page {
  final val address = s"$applicationContext/vrm-locked"
  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)

  final override val title = "Registration number is locked"

  def newDisposal(implicit driver: WebDriver): Element = find(id(BuyAnotherVehicleId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get
}
