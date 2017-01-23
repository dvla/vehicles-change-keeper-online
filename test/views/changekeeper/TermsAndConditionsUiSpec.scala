package views.changekeeper

import composition.TestHarness
import org.scalatest.selenium.WebBrowser.{currentUrl, go}
import pages.changekeeper.TermsAndConditionsPage
import uk.gov.dvla.vehicles.presentation.common.testhelpers.{UiSpec, UiTag}

class TermsAndConditionsUiSpec extends UiSpec with TestHarness {

  "go to page" should {
    "display the page" taggedAs UiTag in new WebBrowserForSelenium {
      go to TermsAndConditionsPage

      currentUrl should equal(TermsAndConditionsPage.url)
    }
  }
}
