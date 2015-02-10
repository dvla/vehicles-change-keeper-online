package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDriver, WebBrowserDSL}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._

class PrivateKeeperDetailsPageSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  private final val Postcode = "qq99qq"
  private final val FirstName = "joe"
  private final val LastName = "bloggs"
  private final val ValidVrn = "BF51BMU"
  private final val ValidDocRefNum = "1" * 11

  def goToPrivateKeeperDetailsPage() = {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter ValidVrn
    VehicleLookupPage.documentReferenceNumber enter ValidDocRefNum
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual PrivateKeeperDetailsPage.title
  }

  def goToSelectNewKeeperAddressPageAfterFillingInNewPrivateKeeper() = {
    goToPrivateKeeperDetailsPage()
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter FirstName
    PrivateKeeperDetailsPage.lastNameTextBox enter LastName
    PrivateKeeperDetailsPage.postcodeTextBox enter Postcode
    click on PrivateKeeperDetailsPage.next
  }

  @Given("^the user is on the Private keeper details page$")
  def the_user_is_on_the_Private_keeper_details_page()  {
    goToPrivateKeeperDetailsPage()
  }

  @When("^the user click on Submit button with out any title selection$")
  def the_user_click_on_Submit_button_with_out_any_title_selection()  {
    PrivateKeeperDetailsPage.assertNoTitleSelected()
    click on PrivateKeeperDetailsPage.next
  }

  @Then("^the user will remain on the same page with an error message \"(.*?)\"$")
  def the_user_will_remain_on_the_same_page_with_an_error_message(validationErrorMessage:String)  {
    page.source should include (validationErrorMessage)
  }

  @When("^the user selects Other title radio button and then click on Submit button$")
  def the_user_selects_Other_title_radio_button_and_then_click_on_Submit_button()  {
    click on PrivateKeeperDetailsPage.other
    click on PrivateKeeperDetailsPage.next
  }

  @When("^not entered any text in Other title text box$")
  def not_entered_any_text_in_Other_title_text_box()  {
    PrivateKeeperDetailsPage.otherText enter ""
  }

  @When("^the user navigates forwards from PrivateKeeper details page and there are no validation errors$")
  def the_user_navigates_forwards_from_PrivateKeeper_details_page_and_there_are_no_validation_errors()  {
    goToSelectNewKeeperAddressPageAfterFillingInNewPrivateKeeper()
  }

  @Then("^the user is taken to the page entitled \"(.*?)\"$")
  def the_user_is_taken_to_the_page_entitled(actualTitleOfThePage:String)  {
    page.source should include (actualTitleOfThePage)
  }

  @When("^the user click on Back link Text$")
  def the_user_click_on_Back_link_Text()  {
    click on PrivateKeeperDetailsPage.back
  }

  @Then("^the user Navigates back from the Private Keeper details page to Vehicle Lookup Page$")
  def the_user_Navigates_back_from_the_Private_Keeper_details_page_to_Vehicle_Lookup_Page()  {
    page.title shouldEqual VehicleLookupPage.title
  }

  @When("^the user click on Submit button by not entering any text on FirstName textBox$")
  def the_user_click_on_Submit_button_by_not_entering_any_text_on_FirstName_textBox()  {
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user click on Submit button with invalid text on FirstName textBox$")
  def the_user_click_on_Submit_button_with_invalid_text_on_FirstName_textBox() {
    PrivateKeeperDetailsPage.firstNameTextBox enter "@@jkhgf"
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user click on Submit button with invalid text on LastName textBox$")
  def the_user_click_on_Submit_button_with_invalid_text_on_LastName_textBox()  {
    PrivateKeeperDetailsPage.lastNameTextBox enter "@£jhgf"
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user click on Submit button by not entering any text on LastName textBox$")
  def the_user_click_on_Submit_button_by_not_entering_any_text_on_LastName_textBox()  {
    PrivateKeeperDetailsPage.lastNameTextBox enter ""
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the user select any title radio button$")
  def the_user_select_any_title_radio_button()  {
    click on PrivateKeeperDetailsPage.mr
  }

  @When("^the total number of characters is equal or less than \"(.*?)\" including title, space and FirstName$")
  def the_total_number_of_characters_is_equal_or_less_than_including_title_space_and_FirstName(d:String)  {
    PrivateKeeperDetailsPage.firstNameTextBox enter "avsreedrtagdtesbgdrewasd"
    val fieldLength:String =  "mr"+' '+PrivateKeeperDetailsPage.firstNameTextBox.value
    fieldLength.length shouldEqual 27
  }

  @When("^click on submit button without any validation errors$")
  def click_on_submit_button_without_any_validation_errors()  {
    PrivateKeeperDetailsPage.lastNameTextBox enter "ghjgj"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99qq"
    click on PrivateKeeperDetailsPage.next
  }

  @When("^the total number of characters is more than \"(.*?)\" including title, space and FirstName$")
  def the_total_number_of_characters_is_more_than_including_title_space_and_FirstName(n:String)  {
    PrivateKeeperDetailsPage.firstNameTextBox enter "avsreedrtagdtesbgdrewasdjhkhh"
    val fieldLength:String =  "mr"+' '+PrivateKeeperDetailsPage.firstNameTextBox.value
    fieldLength.length shouldNot equal(27)
    click on PrivateKeeperDetailsPage.next
  }
}
