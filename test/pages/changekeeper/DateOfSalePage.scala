package pages.changekeeper

import models.DateOfSaleFormModel.Form.{DateOfSaleId, MileageId, TodaysDateId}
import org.openqa.selenium.WebDriver
import org.scalatest.ShouldMatchers
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.CompleteAndConfirm.{BackId, SubmitId}
import org.scalatest.selenium.WebBrowser.{TelField, telField, click, go, find, id, Element, pageSource}

object DateOfSalePage extends Page with ShouldMatchers {
  final val address = buildAppUrl("date-of-sale")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Sale details"

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
    go to DateOfSalePage

    pageSource should(
      include(BackId) and
      include(SubmitId) and
      include(MileageId) and
      include(s"$DateOfSaleId" + "_day") and
      include(s"$DateOfSaleId" + "_month") and
      include(s"$DateOfSaleId" + "_year")
    )

    mileageTextBox.value = mileage
    dayDateOfSaleTextBox.value = dayDateOfSale
    monthDateOfSaleTextBox.value = monthDateOfSale
    yearDateOfSaleTextBox.value = yearDateOfSale

    click on next
  }
}
