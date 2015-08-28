package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.MicroServiceError.{ExitId, TryAgainId}

object MicroServiceErrorPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("service-error")
  final override val title = "We are sorry"

  override def url: String = WebDriverFactory.testUrl + address.substring(1)

  def tryAgain(implicit driver: WebDriver): Element = find(id(TryAgainId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get
}
