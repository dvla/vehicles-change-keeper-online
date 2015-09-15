package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDriver}
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element}

class DateOfSalePageSteps(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "BF51BOV"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    page.title shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "retail"
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value="0"
    click on NewKeeperChooseYourAddressPage.next
    page.title shouldBe DateOfSalePage.title withClue trackingId
  }

  @Given("^that the user is on the date of sale page$")
  def that_the_user_is_on_the_date_of_sale_page() {
    goToDateOfSalePage()
  }

  @Given("^there is a  label titled \"(.*?)\"$")
  def there_is_a_label_titled(lableText: String) {
    page.text.contains(lableText) shouldBe true withClue trackingId
  }

  @Then("^there is a control for entry of the vehicle mileage using the format N\\((\\d+)\\)$")
  def there_is_a_control_for_entry_of_the_vehicle_mileage_using_the_format_N(onlySixDigits: Int) {
    DateOfSalePage.mileageTextBox enter "fhgjhhhkj"
  }

  @When("^there is a labelled Date of Sale and hint text$")
  def there_is_a_labelled_Date_of_Sale_and_hint_text() {
    webDriver.getPageSource.contains("Date of sale") shouldBe true withClue trackingId
  }

  @When("^the Date of sale section will contain the Month label Month entry control Year label Year entry control$")
  def the_Date_of_sale_section_will_contain_the_Month_label_Month_entry_control_Year_label_Year_entry_control(): Unit = {
    webDriver.getPageSource.contains("Day") shouldBe true withClue trackingId
    webDriver.getPageSource.contains("Month") shouldBe true withClue trackingId
    webDriver.getPageSource.contains("Year") shouldBe true withClue trackingId
  }

  @When("^the user selects the data entry control labelled Day$")
  def the_user_selects_the_data_entry_control_labelled_Day() {
    click on DateOfSalePage.dayDateOfSaleTextBox
  }

  @Then("^the user can enter the (\\d+) or (\\d+) digit day of the month$")
  def the_user_can_enter_the_or_digit_day_of_the_month(digitOne:Int,digitTwo:Int): Unit = {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
  }

  @Then("^the field will only accept the values (\\d+)-(\\d+)$")
  def the_field_will_only_accept_the_values(digitOne:Int,digitTwo:Int) {
  }

  @When("^the user selects the data entry control labelled Month$")
  def the_user_selects_the_data_entry_control_labelled_Month() {
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
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
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
  }

  @When("^the Date of sale is in the future$")
  def the_Date_of_sale_is_in_the_future() {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2025"
  }

  @When("^the user click on the next button$")
  def the_user_click_on_the_next_button()  {
    click on CompleteAndConfirmPage.next
  }

  @When("^the Date of sale is incomplete$")
  def the_Date_of_sale_is_incomplete() {
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2025"
  }

  @When("^the Date of sale is not a valid gregorian date$")
  def the_Date_of_sale_is_not_a_valid_gregorian_date() {
  }
}
