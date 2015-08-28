package pages.changekeeper

import org.openqa.selenium.WebDriver
import uk.gov.dvla.vehicles.presentation.common.helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.VehicleLookupFailure
import VehicleLookupFailure.{BeforeYouStartId, VehicleLookupId}

object VehicleLookupFailurePage extends Page with WebBrowserDSL {
  final val address = s"$applicationContext/vehicle-lookup-failure"
  final override val title: String = "Unable to find a vehicle record"

  override def url: String = WebDriverFactory.testUrl + address.substring(1)

  def beforeYouStart(implicit driver: WebDriver): Element = find(id(BeforeYouStartId)).get

  def vehicleLookup(implicit driver: WebDriver): Element = find(id(VehicleLookupId)).get

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)
}
