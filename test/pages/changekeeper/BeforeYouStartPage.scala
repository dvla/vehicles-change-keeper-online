package pages.changekeeper

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{find, id}
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.BeforeYouStart.NextId

object BeforeYouStartPage extends Page {
  final val address = buildAppUrl("before-you-start")
  final override val title: String = "Private sale of a vehicle"
  // final val titleCy: String = TODO set value when Welsh translation complete

  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)

  def startNow(implicit driver: WebDriver) = find(id(NextId)).get
}
