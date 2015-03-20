package pages.changekeeper

import models.DateOfSaleFormModel.Form._
import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser._
import views.changekeeper.CompleteAndConfirm._

object DateOfSalePage extends Page with WebBrowserDSL {
  final val address = buildAppUrl("date-of-sale")
  override def url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Date of sale"

  final val MileageValid = "1000"
  final val DayDateOfSaleValid = "19"
  final val MonthDateOfSaleValid = "10"
  final val YearDateOfSaleValid = "2012"

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def useTodaysDate(implicit driver: WebDriver): Element = find(id(TodaysDateId)).get

  def mileageTextBox(implicit driver: WebDriver): TelField = telField(id(MileageId))

  def dayDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_day"))

  def monthDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_month"))

  def yearDateOfSaleTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfSaleId" + "_year"))

  def navigate(mileage: String = MileageValid,
               dayDateOfSale: String = DayDateOfSaleValid,
               monthDateOfSale: String = MonthDateOfSaleValid,
               yearDateOfSale: String = YearDateOfSaleValid)(implicit driver: WebDriver) = {
    go to CompleteAndConfirmPage

    mileageTextBox enter mileage
    dayDateOfSaleTextBox enter dayDateOfSale
    monthDateOfSaleTextBox enter monthDateOfSale
    yearDateOfSaleTextBox enter yearDateOfSale

    click on next
  }
}
