package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{When, Given}
import cucumber.api.scala.{EN, ScalaDsl}
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{WithClue, WebBrowserDriver}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle}

class NewKeeperChooseYourAddressSteps(webBrowserDriver: WebBrowserDriver)
  extends ScalaDsl with EN with Matchers with WithClue {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]
  private final val ValidVrn = RandomVrmGenerator.uniqueVrm
  private final val ValidDocRefNum = "1" * 11

  def goToNewKeeperChooseYourAddressPage() = {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = ValidVrn
    VehicleLookupPage.documentReferenceNumber.value = ValidDocRefNum
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "retail"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
  }

  def goToEnterAddressManuallyPage() = {
    goToNewKeeperChooseYourAddressPage()
    click on NewKeeperChooseYourAddressPage.manualAddress
    pageTitle should equal(NewKeeperEnterAddressManuallyPage.title) withClue trackingId
  }

  @Given("^the user is on the NewKeeper choose your address page$")
  def the_user_is_on_the_NewKeeper_choose_your_address_page()  {
    goToNewKeeperChooseYourAddressPage()
  }

  @When("^the user navigates forwards from NewKeeper choose your address page and there are no validation errors$")
  def the_user_navigates_forwards_from_NewKeeper_choose_your_address_page_and_there_are_no_validation_errors()  {
    click on NewKeeperChooseYourAddressPage.select
    NewKeeperChooseYourAddressPage.chooseAddress.value = NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
  }

  @When("^the user navigates forwards from NewKeeper choose your address page to the enter address manually page$")
  def the_user_navigates_forwards_from_NewKeeper_choose_your_address_page_to_the_enter_address_manually_page() = {
    goToEnterAddressManuallyPage()
  }

  @When("^the user navigates backwards from the NewKeeper choose your address page$")
  def the_user_navigates_backwards_from_the_NewKeeper_choose_your_address_page()  {
    click on NewKeeperChooseYourAddressPage.back
  }

  @When("^the user has not selected an address on the Select new keeper address page and click on Next button$")
  def the_user_has_not_selected_an_address_on_the_Select_new_keeper_address_page_and_click_on_Next_button()  {
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    click on NewKeeperChooseYourAddressPage.next
  }
}
