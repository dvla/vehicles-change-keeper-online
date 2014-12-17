package pages.common

import uk.gov.dvla.vehicles.presentation.common.helpers
import helpers.webbrowser.WebBrowserDSL
import org.openqa.selenium.{By, WebDriver}

object Accessibility extends WebBrowserDSL {
  def ariaRequiredPresent(controlName: String)(implicit driver: WebDriver): Boolean =
    driver.findElement(By.id(controlName)).getAttribute("aria-required").toBoolean

  def ariaInvalidPresent(controlName: String)(implicit driver: WebDriver): Boolean =
    driver.findElement(By.id(controlName)).getAttribute("aria-invalid").toBoolean
}
