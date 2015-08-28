package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, Element, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.ChangeKeeperSuccess.FinishId

object ChangeKeeperSuccessPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("change-keeper-success")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
  def finish(implicit driver: WebDriver): Element = find(id(FinishId)).get
}
