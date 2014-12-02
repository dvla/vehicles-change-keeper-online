package pages.changekeeper

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.VrmLocked
import VrmLocked.{ExitId, BuyAnotherVehicleId}
import org.openqa.selenium.WebDriver
import pages.ApplicationContext.applicationContext

object VrmLockedPage extends Page with WebBrowserDSL {
  final val address = s"$applicationContext/vrm-locked"
  override def url: String = WebDriverFactory.testUrl + address.substring(1)

  final override val title = "Registration number is locked"

  def newDisposal(implicit driver: WebDriver): Element = find(id(BuyAnotherVehicleId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get
}