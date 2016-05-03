package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Page, WebDriverFactory}
import views.changekeeper.VehicleLookupFailure
import VehicleLookupFailure.{BeforeYouStartId, VehicleLookupId}
import org.scalatest.selenium.WebBrowser.{TextField, textField, TelField, telField, RadioButton, radioButton, click, go, find, id, Element, tagName}

object VehicleLookupFailurePage extends Page {
  final val address = s"$applicationContext/vehicle-lookup-failure"
  final override val title: String = "Unable to find a vehicle record"

  override lazy val url: String = WebDriverFactory.testUrl + address.substring(1)

  def beforeYouStart(implicit driver: WebDriver): Element = find(id(BeforeYouStartId)).get

  def vehicleLookup(implicit driver: WebDriver): Element = find(id(VehicleLookupId)).get

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)
}
