package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class CompleteAndConfirmSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToCompletAndConfirmPage(){
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "BF51BOV"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    page.title shouldEqual BusinessKeeperDetailsPage.title
    BusinessKeeperDetailsPage.businessNameField enter "retail"
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value="0"
    click on NewKeeperChooseYourAddressPage.next
    page.title shouldBe CompleteAndConfirmPage.title
  }

  @Given("^that the user is on the complete and confirm page$")
  def that_the_user_is_on_the_complete_and_confirm_page() {
    goToCompletAndConfirmPage()
  }

  @Given("^there is a  label titled \"(.*?)\"$")
  def there_is_a_label_titled(lableText: String) {
    page.text.contains(lableText) shouldBe true
  }

  @Then("^there is a control for entry of the vehicle mileage using the format N\\((\\d+)\\)$")
  def there_is_a_control_for_entry_of_the_vehicle_mileage_using_the_format_N(onlySixDigits: Int) {
    CompleteAndConfirmPage.mileageTextBox enter "fhgjhhhkj"
  }

  @When("^there is a labelled Date of Sale and hint text$")
  def there_is_a_labelled_Date_of_Sale_and_hint_text(): Unit = {
    webDriver.getPageSource.contains("Date of sale") shouldBe true
  }

  @When("^the Date of sale section will contain the Month label Month entry control Year label Year entry control$")
  def the_Date_of_sale_section_will_contain_the_Month_label_Month_entry_control_Year_label_Year_entry_control(): Unit = {
    webDriver.getPageSource.contains("Day") shouldBe true
    webDriver.getPageSource.contains("Month") shouldBe true
    webDriver.getPageSource.contains("Year") shouldBe true
  }

  @When("^the user selects the data entry control labelled Day$")
  def the_user_selects_the_data_entry_control_labelled_Day(): Unit = {
    click on CompleteAndConfirmPage.dayDateOfSaleTextBox
  }

  @Then("^the user can enter the (\\d+) or (\\d+) digit day of the month$")
  def the_user_can_enter_the_or_digit_day_of_the_month(digitOne:Int,digitTwo:Int): Unit = {
    CompleteAndConfirmPage.dayDateOfSaleTextBox enter "12"
  }

  @Then("^the field will only accept the values (\\d+)-(\\d+)$")
  def the_field_will_only_accept_the_values(digitOne:Int,digitTwo:Int) {
  }

  @When("^the user selects the data entry control labelled Month$")
  def the_user_selects_the_data_entry_control_labelled_Month(): Unit = {
    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
  }

  @Then("^the user can enter the (\\d+) or (\\d+) digit month of the year$")
  def the_user_can_enter_the_or_digit_month_of_the_year(digitOne:Int,digitTwo:Int) {
  }

  @When("^the user selects the data entry control labelled Year$")
  def the_user_selects_the_data_entry_control_labelled_Year(): Unit = {
    click on CompleteAndConfirmPage.yearDateOfSaleTextBox
  }

  @Then("^the user can enter the (\\d+) digit year$")
  def the_user_can_enter_the_digit_year(four: Int): Unit = {
    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2010"
  }

  @When("^the Date of sale is in the future$")
  def the_Date_of_sale_is_in_the_future(): Unit = {
    CompleteAndConfirmPage.dayDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2025"
  }

  @When("^the user is has selected the submit control$")
  def the_user_is_has_selected_the_submit_control(): Unit = {
    click on CompleteAndConfirmPage.next
  }

  @When("^the Date of sale is incomplete$")
  def the_Date_of_sale_is_incomplete(): Unit = {
    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2025"
  }

  @When("^the Date of sale is not a valid gregorian date$")
  def the_Date_of_sale_is_not_a_valid_gregorian_date() {
  }

  @When("^the consent field is not checked$")
  def the_consent_field_is_not_checked() {
  }

  @Then("^the user is not progressed to the next page$")
  def the_user_is_not_progressed_to_the_next_page(): Unit = {
    page.title shouldEqual CompleteAndConfirmPage.title
  }

  @When("^the consent field is checked$")
  def the_consent_field_is_checked() {
    CompleteAndConfirmPage.dayDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.monthDateOfSaleTextBox enter "12"
    CompleteAndConfirmPage.yearDateOfSaleTextBox enter "2010"
    click on CompleteAndConfirmPage.consent
  }

  @Then("^the user is progressed to the next stage of the service$")
  def the_user_is_progressed_to_the_next_stage_of_the_service() {
    page.title shouldEqual ChangeKeeperSuccessPage.title
  }
  @Then("^an error message displayed \"(.*?)\"$")
  def an_error_message_displayed(err:String): Unit =  {
    page.source.contains("Transaction Unsuccessful")
  }
}

