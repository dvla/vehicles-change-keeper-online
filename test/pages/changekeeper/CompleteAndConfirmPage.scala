package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Element, WebDriverFactory, WebBrowserDSL, Page, Checkbox}
import models.CompleteAndConfirmFormModel.Form.ConsentId
import org.openqa.selenium.WebDriver
import views.changekeeper.CompleteAndConfirm.{BackId, SubmitId}

object CompleteAndConfirmPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("complete-and-confirm")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Complete and confirm"

  final val ConsentTrue = "consent"

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def consent(implicit driver: WebDriver): Checkbox = checkbox(id(ConsentId))

  def navigate(consent: String = ConsentTrue)(implicit driver: WebDriver) = {
    go to CompleteAndConfirmPage
    click on consent
    click on next
  }
}
