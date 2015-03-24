package views.changekeeper

import pages.changekeeper.VehicleLookupPage
import composition.TestHarness
import helpers.CookieFactoryForUISpecs
import helpers.webbrowser.ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import helpers.UiSpec
import models.CompleteAndConfirmFormModel
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.common.ErrorPanel
import pages.changekeeper._
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import pages.changekeeper.DateOfSalePage.navigate
import pages.common.Feedback.EmailFeedbackLink

class DateOfSaleIntegrationSpec extends UiSpec with TestHarness {
  private final val ProgressStepNumber = 5

  "go to page" should {
    "display the page for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.title should equal(DateOfSalePage.title)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage

      page.source should include(EmailFeedbackLink)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.source should include(progressStep(ProgressStepNumber))
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage
      page.source should not include(progressStep(ProgressStepNumber))
    }

    "Redirect when no new keeper details are cached" taggedAs UiTag in new WebBrowser {
      go to DateOfSalePage
      page.title should equal(VehicleLookupPage.title)
    }

    "contain the hidden csrfToken field" taggedAs UiTag in new WebBrowser {
      go to DateOfSalePage
      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "display optional for vehicle mileage input" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetup()
      go to DateOfSalePage

      page.source should include("Vehicle mileage")
    }
  }

  "Submit button" should {

    "display one validation error message when a mileage is entered greater than max length for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "1000000")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a mileage is entered less than min length for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "-1")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when a mileage containing letters is entered for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(mileage = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when day date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(dayDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when month date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(monthDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when year date of sale is empty for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(yearDateOfSale = "")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when day date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(dayDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when month date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(monthDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display one validation error message when year date of sale contains letters for a new keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetup()
      navigate(yearDateOfSale = "a")
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  private def cacheSetup()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs.vehicleAndKeeperDetails().newKeeperDetailsModel()
}
