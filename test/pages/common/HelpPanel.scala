package pages.common

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Element, WebBrowserDSL}
import views.common.Help.HelpLinkId

object HelpPanel extends WebBrowserDSL {
  def help(implicit driver: WebDriver): Element = find(id(HelpLinkId)).get
}
