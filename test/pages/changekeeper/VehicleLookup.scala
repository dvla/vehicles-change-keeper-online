package pages.changekeeper

import helpers.webbrowser.{WebDriverFactory, WebBrowserDSL, Page}

object VehicleLookupPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("vehicle-lookup")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Enter vehicle details"
}
