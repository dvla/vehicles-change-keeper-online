package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WebDriverFactory, Page}
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element}
import views.changekeeper.ChangeKeeperSuccess.FinishId

object ChangeKeeperSuccessPage extends Page {
  final val address = buildAppUrl("change-keeper-success")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Summary"
  def finish(implicit driver: WebDriver): Element = find(id(FinishId)).get
}
