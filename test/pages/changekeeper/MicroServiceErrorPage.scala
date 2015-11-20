package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import uk.gov.dvla.vehicles.presentation.common.views.widgets.MicroServiceError.{ExitId, TryAgainId}
import org.scalatest.selenium.WebBrowser.{find, id, Element}

object MicroServiceErrorPage extends Page {
  final val address = buildAppUrl("service-error")
  final override val title = "We are sorry"

  override val url: String = WebDriverFactory.testUrl + address.substring(1)

  def tryAgain(implicit driver: WebDriver): Element = find(id(TryAgainId)).get

  def exit(implicit driver: WebDriver): Element = find(id(ExitId)).get
}
