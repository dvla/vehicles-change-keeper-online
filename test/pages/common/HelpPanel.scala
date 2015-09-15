package pages.common

import org.openqa.selenium.WebDriver
import views.common.Help.HelpLinkId
import org.scalatest.selenium.WebBrowser.{find, id, Element}

object HelpPanel {
  def help(implicit driver: WebDriver): Element = find(id(HelpLinkId)).get
}
