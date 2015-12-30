package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Then, When, Given}
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.ChangeKeeperSuccessPage
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.WebBrowserDriver
import uk.gov.dvla.vehicles.presentation.common.testhelpers.RandomVrmGenerator

class CompleteAndConfirmSteps(webBrowserDriver: WebBrowserDriver)
  extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = vrm
    VehicleLookupPage.documentReferenceNumber.value = "11111111111"
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "retail"
    BusinessKeeperDetailsPage.postcodeField.value = "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    NewKeeperChooseYourAddressPage.chooseAddress.value = NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
    pageTitle shouldBe DateOfSalePage.title withClue trackingId
  }

  def goToCompletAndConfirmPage(vrm: String = RandomVrmGenerator.uniqueVrm) {
    goToDateOfSalePage(vrm)

    import java.util.Calendar

    val c = Calendar.getInstance
    c.add(Calendar.MONTH, -1)

    DateOfSalePage.dayDateOfSaleTextBox.value = f"${c.get(Calendar.DATE)}%02d"
    DateOfSalePage.monthDateOfSaleTextBox.value = f"${c.get(Calendar.MONTH)+1}%02d"
    DateOfSalePage.yearDateOfSaleTextBox.value = c.get(Calendar.YEAR).toString
    click on DateOfSalePage.next
    pageTitle shouldBe CompleteAndConfirmPage.title withClue trackingId
  }

  @Given("^that the user is on the complete and confirm page$")
  def that_the_user_is_on_the_complete_and_confirm_page() {
    goToCompletAndConfirmPage()
  }

  @When("^the user click on confirm sale button$")
  def the_user_click_on_confirm_sale_button()  {
    click on CompleteAndConfirmPage.next
  }

  @Then("^the user is not progressed to the next page$")
  def the_user_is_not_progressed_to_the_next_page(): Unit = {
    pageTitle shouldEqual CompleteAndConfirmPage.title withClue trackingId
  }

  @When("^the consent field is not checked$")
  def the_consent_field_is_not_checked() {
    if(CompleteAndConfirmPage.consent.isSelected) click on CompleteAndConfirmPage.consent
  }

  @When("^the right to registration field is checked$")
  def the_right_to_registration_is_checked(): Unit = {
    click on CompleteAndConfirmPage.regRight
  }

  @When("^the consent field is checked$")
  def the_consent_field_is_checked() {
    click on CompleteAndConfirmPage.consent
  }

  @Then("^the user is progressed to the next stage of the service$")
  def the_user_is_progressed_to_the_next_stage_of_the_service() {
    pageTitle shouldEqual ChangeKeeperSuccessPage.title withClue trackingId
  }
  @Then("^an error message displayed \"(.*?)\"$")
  def an_error_message_displayed(err: String): Unit =  {
    pageSource should include(err)
  }

  @When("^The user clicks back on Complete and Confirm page$")
  def the_user_clicks_back_on_Complete_and_Confirm_page(): Unit = {
    click on CompleteAndConfirmPage.back
  }

  @When("^The user clicks back on Date of sale page$")
  def the_user_clicks_back_on_Date_of_sale_page(): Unit = {
    click on DateOfSalePage.back
  }
}
