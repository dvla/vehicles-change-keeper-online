package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Given, When, Then}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class CheckPreviousKeeperEndDateOrLastKeeperChangeDateAndCompareDateOfSale(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage(registrationNumber:String) = {
    go to VehicleLookupPage
    page.title shouldEqual VehicleLookupPage.title
    VehicleLookupPage.vehicleRegistrationNumber enter registrationNumber
    VehicleLookupPage.documentReferenceNumber enter "88888888881"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    page.title shouldEqual BusinessKeeperDetailsPage.title
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField enter "retail"
    click on BusinessKeeperDetailsPage.emailInvisible
    BusinessKeeperDetailsPage.postcodeField enter "qq99qq"
    click on BusinessKeeperDetailsPage.next
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value="0"
    click on NewKeeperChooseYourAddressPage.next
    page.title shouldBe DateOfSalePage.title
  }

  @Given("""^the user is on the Date of Sale page with Vehicle Registration number as "(.*?)"$""")
  def the_user_is_on_the_Date_of_Sale_page_with_Vehicle_Registration_number_as(registrationNumber:String)  {
    goToDateOfSalePage(registrationNumber)
  }

  @When("^the user enters a date of sale before the previous keeper end date and click on submit button$")
  def the_user_enters_a_date_of_sale_before_the_previous_keeper_end_date_and_click_on_submit_button()  {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
  }

  @When("^the user enters a date of sale before the last keeper change date and click on submit button$")
  def the_user_enters_a_date_of_sale_before_the_last_keeper_change_date_and_click_on_submit_button() {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
  }

  @Then("^the user will remain on the Date of Sale page and a warning will be displayed$")
  def the_user_will_remain_on_the_date_of_sale_page_and_a_warning_will_be_displayed()  {
    page.title should equal(DateOfSalePage.title)
    page.source should include("<div class=\"popup-modal\">")
  }

  @Then("^the user confirms the date$")
  def the_user_confirms_the_date()  {
    click on DateOfSalePage.next
  }

  @Then("^the user will be taken to the \"(.*?)\" page$")
  def the_user_will_be_taken_to_the_complete_and_confirm_page(a:String)  {
    page.title shouldEqual CompleteAndConfirmPage.title
  }

  @Then("^the user will not see a warning message$")
  def the_user_will_not_see_a_warning_message()  {
    page.source should not include "<div class=\"popup-modal\">"
  }

  @When("^no date is returned by the back end system and  user enters a date of sale$")
  def no_date_is_returned_by_the_back_end_system_and_user_enters_a_date_of_sale(): Unit =  {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
  }

  @When("^keeper end date and change date has been returned by the back end system$")
  def keeper_end_date_and_change_date_has_been_returned_by_the_back_end_system()  {
    DateOfSalePage.dayDateOfSaleTextBox enter "12"
    DateOfSalePage.monthDateOfSaleTextBox enter "12"
    DateOfSalePage.yearDateOfSaleTextBox enter "2010"
    click on DateOfSalePage.next
  }

}
