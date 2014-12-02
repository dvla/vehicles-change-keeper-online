package pages.changekeeper

import helpers.webbrowser._
import models.CompleteAndConfirmFormModel.Form.{ConsentId, DateOfSaleId, MileageId, TodaysDateId}
import org.openqa.selenium.WebDriver
import views.changekeeper.CompleteAndConfirm.{BackId, SubmitId}

object CompleteAndConfirmPage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("complete-and-confirm")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Complete and confirm"

  final val MileageValid = "1000"
  final val DayDateOfSaleValid = "19"
  final val MonthDateOfSaleValid = "10"
  final val YearDateOfSaleValid = "2012"
  final val ConsentTrue = "consent"

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def useTodaysDate(implicit driver: WebDriver): Element = find(id(TodaysDateId)).get

  def mileageTextBox(implicit driver: WebDriver): TelField = telField(id(MileageId))

  def dayDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_day"))

  def monthDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_month"))

  def yearDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_year"))

  def consent(implicit driver: WebDriver): Checkbox = checkbox(id(ConsentId))

  def navigate(mileage: String = MileageValid,
               dayDateOfSale: String = DayDateOfSaleValid,
               monthDateOfSale: String = MonthDateOfSaleValid,
               yearDateOfSale: String = YearDateOfSaleValid,
               consent: String = ConsentTrue)(implicit driver: WebDriver) = {
    go to CompleteAndConfirmPage

    mileageTextBox enter mileage
    dayDateOfSaleTextBox enter dayDateOfSale
    monthDateOfSaleTextBox enter monthDateOfSale
    yearDateOfSaleTextBox enter yearDateOfSale
    click on consent

    click on next
  }
}
