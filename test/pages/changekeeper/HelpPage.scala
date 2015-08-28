package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.common.Help.{BackId, ExitId}

object HelpPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("help")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Help"

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get
}
