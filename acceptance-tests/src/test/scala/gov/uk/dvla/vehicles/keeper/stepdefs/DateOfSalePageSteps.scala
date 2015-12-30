package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import org.joda.time.LocalDate
import org.openqa.selenium.WebDriver
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class DateOfSalePageSteps(webBrowserDriver: WebBrowserDriver)
  extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  private val commonSteps = new CommonSteps(webBrowserDriver)

  @Given("^that the user is on the date of sale page$")
  def that_the_user_is_on_the_date_of_sale_page() {
    commonSteps.goToDateOfSalePage()
  }

  @Given("^there is a  label titled \"(.*?)\"$")
  def there_is_a_label_titled(lableText: String) {
    // need to use page text rather than source
    pageSource.contains(lableText) shouldBe true withClue trackingId
  }

  @Then("^there is a control for entry of the vehicle mileage using the format N\\((\\d+)\\)$")
  def there_is_a_control_for_entry_of_the_vehicle_mileage_using_the_format_N(maxDigits: Int) {
    DateOfSalePage.mileageTextBox.attribute("maxlength") shouldBe Some(maxDigits.toString)
  }

  @When("^there is a labelled Date of Sale and hint text$")
  def there_is_a_labelled_Date_of_Sale_and_hint_text() {
    webDriver.getPageSource.contains("Date of sale") shouldBe true withClue trackingId
  }

  @When("^the Date of sale section will contain the Month label Month entry control Year label Year entry control$")
  def the_Date_of_sale_section_will_contain_the_Month_label_Month_entry_control_Year_label_Year_entry_control(): Unit = {
    val source = pageSource
    source.contains("Day") shouldBe true withClue trackingId
    source.contains("Month") shouldBe true withClue trackingId
    source.contains("Year") shouldBe true withClue trackingId
  }

  @When("^the user selects the data entry control labelled Day$")
  def the_user_selects_the_data_entry_control_labelled_Day() {
    click on DateOfSalePage.dayDateOfSaleTextBox
  }

  @Then("^the user can enter the (\\d+) or (\\d+) digit day of the month$")
  def the_user_can_enter_the_or_digit_day_of_the_month(digitOne:Int,digitTwo:Int): Unit = {
    DateOfSalePage.dayDateOfSaleTextBox.value = "12"
  }

  @Then("^the field will only accept the values (\\d+)-(\\d+)$")
  def the_field_will_only_accept_the_values(digitOne:Int,digitTwo:Int) {
  }

  @When("^the user selects the data entry control labelled Month$")
  def the_user_selects_the_data_entry_control_labelled_Month() {
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
  }

  @Then("^the user can enter the (\\d+) or (\\d+) digit month of the year$")
  def the_user_can_enter_the_or_digit_month_of_the_year(digitOne:Int,digitTwo:Int) {
  }

  @When("^the user selects the data entry control labelled Year$")
  def the_user_selects_the_data_entry_control_labelled_Year() {
    click on DateOfSalePage.yearDateOfSaleTextBox
  }

  @Then("^the user can enter the (\\d+) digit year$")
  def the_user_can_enter_the_digit_year(four: Int) {
    DateOfSalePage.yearDateOfSaleTextBox.value = org.joda.time.LocalDate.now.minusYears(2).getYear.toString
  }

  @When("^the Date of sale is in the past$")
  def the_Date_of_sale_is_in_the_past() {
    val oldDate = new LocalDate().minusYears(5).minusDays(1)
    DateOfSalePage.dayDateOfSaleTextBox.value = oldDate.toString("dd")
    DateOfSalePage.monthDateOfSaleTextBox.value = oldDate.toString("MM")
    DateOfSalePage.yearDateOfSaleTextBox.value = oldDate.getYear.toString
  }

  @When("^the Date of sale is in the future$")
  def the_Date_of_sale_is_in_the_future() {
    val tomorrow = new LocalDate().plusDays(1)
    DateOfSalePage.dayDateOfSaleTextBox.value = tomorrow.toString("dd")
    DateOfSalePage.monthDateOfSaleTextBox.value = tomorrow.toString("MM")
    DateOfSalePage.yearDateOfSaleTextBox.value = tomorrow.getYear.toString
  }

  @When("^the user click on the next button$")
  def the_user_click_on_the_next_button()  {
    click on CompleteAndConfirmPage.next
  }

  @When("^the Date of sale is incomplete$")
  def the_Date_of_sale_is_incomplete() {
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
    DateOfSalePage.yearDateOfSaleTextBox.value = LocalDate.now.minusYears(1).getYear.toString
  }

  @When("^the Date of sale is not a valid gregorian date$")
  def the_Date_of_sale_is_not_a_valid_gregorian_date() {
    DateOfSalePage.dayDateOfSaleTextBox.value = "29"
    DateOfSalePage.monthDateOfSaleTextBox.value = "02"
    DateOfSalePage.yearDateOfSaleTextBox.value = LocalDate.now.minusYears(1).getYear.toString
  }
}
