package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.BeforeYouStart
import BeforeYouStart.NextId
import org.scalatest.selenium.WebBrowser.{find, id, Element}

object BeforeYouStartPage extends Page {
  final val address = buildAppUrl("before-you-start")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Private sale of a vehicle"
  // final val titleCy: String = TODO set value when Welsh translation complete

  def startNow(implicit driver: WebDriver): Element = find(id(NextId)).get
}
