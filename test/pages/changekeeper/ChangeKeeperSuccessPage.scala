package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Page, Element,WebBrowserDSL, WebDriverFactory}
import org.openqa.selenium.WebDriver
import views.changekeeper.ChangeKeeperSuccess.FinishId


object ChangeKeeperSuccessPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("change-keeper-success")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
//  final val titleCy: String = "Cael gwared cerbyd i mewn i'r fasnach foduron"

  def finish(implicit driver: WebDriver): Element = find(id(FinishId)).get

}
