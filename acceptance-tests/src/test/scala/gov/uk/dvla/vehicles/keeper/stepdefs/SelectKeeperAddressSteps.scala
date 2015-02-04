package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{WebBrowserDSL, WebBrowserDriver}

class SelectKeeperAddressSteps(webBrowserDriver: WebBrowserDriver) extends ScalaDsl with EN with WebBrowserDSL with Matchers {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToSelectKeeperAddressPage() {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber enter "BF51BVB"
    VehicleLookupPage.documentReferenceNumber enter "11111111111"
    click on VehicleLookupPage.vehicleSoldToPrivateIndividual
    click on VehicleLookupPage.next
    page.title shouldEqual PrivateKeeperDetailsPage.title
    click on PrivateKeeperDetailsPage.mr
    PrivateKeeperDetailsPage.firstNameTextBox enter "tue"
    PrivateKeeperDetailsPage.lastNameTextBox enter "nny"
    PrivateKeeperDetailsPage.postcodeTextBox enter "qq99qq"
    click on PrivateKeeperDetailsPage.next
    page.title should equal(NewKeeperChooseYourAddressPage.title)
  }

  @Given("^the user has not selected an address on the Select new keeper address page$")
  def the_user_has_not_selected_an_address_on_the_Select_new_keeper_address_page()  {
    goToSelectKeeperAddressPage()
  }

  @When("^the user selects Next button$")
  def the_user_selects_Next_button()  {
    click on NewKeeperChooseYourAddressPage.next
  }

  @Then("^the user is presented with an error message \"(.*?)\"$")
  def the_user_is_presented_with_an_error_message(errMsg:String)  {
    NewKeeperChooseYourAddressPage.errorTextForTitle(errMsg) shouldBe true
  }

  @Given("^the user has selected an address from the returned lookup on the Select new keeper address page$")
  def the_user_has_selected_an_address_from_the_returned_lookup_on_the_Select_new_keeper_address_page()  {
    goToSelectKeeperAddressPage()
    NewKeeperChooseYourAddressPage.chooseAddress.value = "0"
  }

  @Then("^the user is progressed to the next page$")
  def the_user_is_progressed_to_the_next_page()  {
    page.title should equal(CompleteAndConfirmPage.title)
  }

  @Given("^the user is on the Select new keeper address page$")
  def the_user_is_on_the_Select_new_keeper_address_page()  {
    goToSelectKeeperAddressPage()
  }

  @When("^the user selects Back button$")
  def the_user_selects_Back_button()  {
    click on NewKeeperChooseYourAddressPage.back
  }

  @Then("^the user is progressed to the previous page$")
  def the_user_is_progressed_to_the_previous_page()  {
    page.title should equal(PrivateKeeperDetailsPage.title)
  }

  @When("^the user selects Address not listed link$")
  def the_user_selects_Address_not_listed_link()  {
    click on NewKeeperChooseYourAddressPage.manualAddress
  }

  @Then("^the user is progressed to the Manual Address page$")
  def the_user_is_progressed_to_the_Manual_Address_page()  {
     page.title should equal(NewKeeperEnterAddressManuallyPage.title)
  }

}
