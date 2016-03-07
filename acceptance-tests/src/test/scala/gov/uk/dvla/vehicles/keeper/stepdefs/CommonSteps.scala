package gov.uk.dvla.vehicles.keeper.stepdefs

import cucumber.api.java.en.{Given, When, Then}
import org.scalatest.selenium.WebBrowser.pageTitle
import org.scalatest.selenium.WebBrowser.pageSource
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.go
import org.openqa.selenium.WebDriver
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.VehicleLookupPage
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.WebBrowserDriver
import common.testhelpers.RandomVrmGenerator

class CommonSteps(webBrowserDriver: WebBrowserDriver) extends gov.uk.dvla.vehicles.keeper.helpers.AcceptanceTestHelper {

  implicit lazy val webDriver = webBrowserDriver.asInstanceOf[WebDriver]

  def goToDateOfSalePage(vrm: String = RandomVrmGenerator.uniqueVrm,
                         docRef: String = "11111111111") {
    go to VehicleLookupPage
    VehicleLookupPage.vehicleRegistrationNumber.value = vrm
    VehicleLookupPage.documentReferenceNumber.value = docRef
    click on VehicleLookupPage.emailInvisible
    click on VehicleLookupPage.vehicleSoldToBusiness
    click on VehicleLookupPage.next
    pageTitle shouldEqual BusinessKeeperDetailsPage.title withClue trackingId
    click on BusinessKeeperDetailsPage.fleetNumberInvisible
    BusinessKeeperDetailsPage.businessNameField.value = "retail"
    BusinessKeeperDetailsPage.postcodeField.value =  "qq99qq"
    click on BusinessKeeperDetailsPage.emailInvisible
    click on BusinessKeeperDetailsPage.next
    pageTitle shouldEqual NewKeeperChooseYourAddressPage.title withClue trackingId
    NewKeeperChooseYourAddressPage.chooseAddress.value = NewKeeperChooseYourAddressPage.defaultSelectedAddress
    click on NewKeeperChooseYourAddressPage.next
    pageTitle shouldEqual DateOfSalePage.title withClue trackingId
  }
}
