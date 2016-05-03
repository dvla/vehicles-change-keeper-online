package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers
import models.CompleteAndConfirmFormModel.Form.{ConsentId, RegRightId}
import org.openqa.selenium.WebDriver
import views.changekeeper.CompleteAndConfirm.{BackId, SubmitId}
import helpers.webbrowser.{WebDriverFactory, Page}
import org.scalatest.selenium.WebBrowser.{Checkbox, checkbox, click, go, find, id, Element}

object CompleteAndConfirmPage extends Page {
  final val address = buildAppUrl("complete-and-confirm")
  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Complete and confirm"

  final val ConsentTrue = "consent"

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def consent(implicit driver: WebDriver): Checkbox = checkbox(id(ConsentId))

  def regRight(implicit  driver: WebDriver): Checkbox = checkbox(id(RegRightId))

  def navigate(consent: String = ConsentTrue)(implicit driver: WebDriver) = {
    go to CompleteAndConfirmPage
    click on consent
    click on regRight
    click on next
  }
}
