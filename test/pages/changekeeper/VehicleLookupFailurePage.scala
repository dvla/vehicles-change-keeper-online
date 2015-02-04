package pages.changekeeper

import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.{Element, Page, WebBrowserDSL, WebDriverFactory}
import views.changekeeper.VehicleLookupFailure
import VehicleLookupFailure.{BeforeYouStartId, VehicleLookupId}
import org.openqa.selenium.WebDriver
//import pages.ApplicationContext.applicationContext

object VehicleLookupFailurePage extends Page with WebBrowserDSL {
  final val address = s"$applicationContext/vehicle-lookup-failure"
  final override val title: String = "Look-up was unsuccessful"

  override def url: String = WebDriverFactory.testUrl + address.substring(1)

  def beforeYouStart(implicit driver: WebDriver): Element = find(id(BeforeYouStartId)).get

  def vehicleLookup(implicit driver: WebDriver): Element = find(id(VehicleLookupId)).get

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

}
