package pages.changekeeper

import helpers.webbrowser.{WebDriverFactory, WebBrowserDSL, Page}

object SetupTradeDetailsPage extends Page with WebBrowserDSL {
  final val TraderBusinessNameValid = "example trader name"
  final val PostcodeWithoutAddresses = "xx99xx"
  final val PostcodeValid = "QQ99QQ"
  final val TraderEmailValid = "example@example.co.uk"

  final val address = buildAppUrl("setup-trade-details")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Provide trader details"
}
