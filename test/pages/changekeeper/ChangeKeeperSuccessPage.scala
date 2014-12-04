package pages.changekeeper

import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import org.openqa.selenium.WebDriver

object ChangeKeeperSuccessPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("change-keeper-success")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
//  final val titleCy: String = "Cael gwared cerbyd i mewn i'r fasnach foduron"
}
