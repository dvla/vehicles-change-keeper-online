package pages.changekeeper

import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.tagName
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.TelField
import org.scalatest.selenium.WebBrowser.telField
import org.scalatest.selenium.WebBrowser.RadioButton
import org.scalatest.selenium.WebBrowser.radioButton
import PrivateKeeperDetailsPage.PostcodeValid
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.{Page, WebDriverFactory}
import common.views.models.AddressAndPostcodeViewModel.Form.PostcodeId
import common.views.models.AddressLinesViewModel
import AddressLinesViewModel.Form.{AddressLinesId, BuildingNameOrNumberId, Line2Id, Line3Id, PostTownId}
import common.model.NewKeeperEnterAddressManuallyFormModel.Form.AddressAndPostcodeId
import views.changekeeper.NewKeeperEnterAddressManually.{BackId, NextId}
import webserviceclients.fakes.FakeAddressLookupService.BuildingNameOrNumberValid
import webserviceclients.fakes.FakeAddressLookupService.Line2Valid
import webserviceclients.fakes.FakeAddressLookupService.Line3Valid
import webserviceclients.fakes.FakeAddressLookupService.PostTownValid

object NewKeeperEnterAddressManuallyPage extends Page {
  final val address = buildAppUrl("new-keeper-enter-address-manually")
  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Enter address"

  def addressBuildingNameOrNumber(implicit driver: WebDriver): TextField =
    textField(id(s"${AddressAndPostcodeId}_${AddressLinesId}_$BuildingNameOrNumberId"))

  def addressLine2(implicit driver: WebDriver): TextField =
    textField(id(s"${AddressAndPostcodeId}_${AddressLinesId}_$Line2Id"))

  def addressLine3(implicit driver: WebDriver): TextField =
    textField(id(s"${AddressAndPostcodeId}_${AddressLinesId}_$Line3Id"))

  def addressPostTown(implicit driver: WebDriver): TextField =
    textField(id(s"${AddressAndPostcodeId}_${AddressLinesId}_$PostTownId"))

  def addressPostcode(implicit driver: WebDriver): TextField =
    textField(id(s"${AddressAndPostcodeId}_$PostcodeId"))

  def next(implicit driver: WebDriver): Element = find(id(NextId)).get

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def errorTextForFields(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

  def happyPath(buildingNameOrNumber: String = BuildingNameOrNumberValid,
                line2: String = Line2Valid,
                line3: String = Line3Valid,
                postTown: String = PostTownValid,
                postcode: String = PostcodeValid)
               (implicit driver: WebDriver) = {
    go to NewKeeperEnterAddressManuallyPage
    addressBuildingNameOrNumber.value = buildingNameOrNumber
    addressLine2.value = line2
    addressLine3.value = line3
    addressPostTown.value = postTown
    addressPostcode.value = postcode
    click on next
  }

  def happyPathMandatoryFieldsOnly(buildingNameOrNumber: String = BuildingNameOrNumberValid,
                                   postTown: String = PostTownValid,
                                   postcode: String = PostcodeValid)
                                  (implicit driver: WebDriver) = {
    go to NewKeeperEnterAddressManuallyPage
    addressBuildingNameOrNumber.value = buildingNameOrNumber
    addressPostTown.value = postTown
    addressPostcode.value = postcode
    click on next
  }

  def sadPath(implicit driver: WebDriver) = {
    go to NewKeeperEnterAddressManuallyPage
    click on next
  }
}
