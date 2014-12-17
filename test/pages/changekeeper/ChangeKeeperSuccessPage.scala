package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Page, WebBrowserDSL, WebDriverFactory}

object ChangeKeeperSuccessPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("change-keeper-success")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
//  final val titleCy: String = "Cael gwared cerbyd i mewn i'r fasnach foduron"
}
