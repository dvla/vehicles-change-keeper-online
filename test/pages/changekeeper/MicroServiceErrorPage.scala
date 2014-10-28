package pages.changekeeper

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.MicroServiceError
import MicroServiceError.{ExitId, TryAgainId}
import org.openqa.selenium.WebDriver

object MicroServiceErrorPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("service-error")
  final override val title = "We are sorry"

  override val url: String = WebDriverFactory.testUrl + address.substring(1)

  def tryAgain(implicit driver: WebDriver): Element = find(id(TryAgainId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get
}