package gov.uk.dvla.vehicles.keeper.stepdefs

import java.util.Calendar

import cucumber.api.java.en.{Given, When, Then}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDriver}
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class CheckPreviousKeeperEndDateOrLastKeeperChangeDateAndCompareDateOfSale(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  val dos = Calendar.getInstance()
  dos.add(Calendar.YEAR, -1)

  def goToDateOfSalePage(registrationNumber:String) = {
    go to VehicleLookupPage
    pageTitle shouldEqual VehicleLookupPage.title withClue trackingId
    VehicleLookupPage.vehicleRegistrationNumber.value = registrationNumber
    VehicleLookupPage.documentReferenceNumber.value = "88888888881"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "retail"
    click on BusinessKeeperDetailsPage.emailInvisible
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.next
    NewKeeperChooseYourAddressPage.chooseAddress.value=NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
    pageTitle shouldBe DateOfSalePage.title withClue trackingId
  }

  @Given("^The user goes to the Date of sale page entering registration number: (.*?)$")
  def the_user_is_on_the_Date_of_Sale_page_with_Vehicle_Registration_number_as(registrationNumber:String)  {
    goToDateOfSalePage(registrationNumber)
  }

  @When("^the user enters a date of sale before the previous keeper end date and click on submit button$")
  def the_user_enters_a_date_of_sale_before_the_previous_keeper_end_date_and_click_on_submit_button()  {
    DateOfSalePage.dayDateOfSaleTextBox.value = "12"
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
    DateOfSalePage.yearDateOfSaleTextBox.value = getPreviousYear
    click on DateOfSalePage.next
  }

  @When("^the user enters a date of sale before the last keeper change date and click on submit button$")
  def the_user_enters_a_date_of_sale_before_the_last_keeper_change_date_and_click_on_submit_button() {
    DateOfSalePage.dayDateOfSaleTextBox.value = "12"
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
    DateOfSalePage.yearDateOfSaleTextBox.value = getPreviousYear
    click on DateOfSalePage.next
  }

  @Then("^the user will remain on the Date of Sale page and a warning will be displayed$")
  def the_user_will_remain_on_the_date_of_sale_page_and_a_warning_will_be_displayed()  {
    pageTitle should equal(DateOfSalePage.title) withClue trackingId
    pageSource should include("<div class=\"popup-modal\">") withClue trackingId
  }

  @Then("^the user confirms the date$")
  def the_user_confirms_the_date()  {
    click on DateOfSalePage.next
  }

  @Then("^the user will be taken to the \"(.*?)\" page$")
  def the_user_will_be_taken_to_the_complete_and_confirm_page(a:String)  {
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
  }

  @Then("^the user will not see a warning message$")
  def the_user_will_not_see_a_warning_message()  {
    pageSource should not include "<div class=\"popup-modal\">" withClue trackingId
  }

  @When("^no date is returned by the back end system and user enters a date of sale$")
  def no_date_is_returned_by_the_back_end_system_and_user_enters_a_date_of_sale(): Unit =  {
    DateOfSalePage.dayDateOfSaleTextBox.value = "12"
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
    DateOfSalePage.yearDateOfSaleTextBox.value = getPreviousYear
    click on DateOfSalePage.next
  }

  @When("^keeper end date and change date has been returned by the back end system$")
  def keeper_end_date_and_change_date_has_been_returned_by_the_back_end_system()  {
    DateOfSalePage.dayDateOfSaleTextBox.value = "12"
    DateOfSalePage.monthDateOfSaleTextBox.value = "12"
    DateOfSalePage.yearDateOfSaleTextBox.value = getPreviousYear
    click on DateOfSalePage.next
  }

  private def getPreviousYear = {
    (java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)-1).toString
  }
}
