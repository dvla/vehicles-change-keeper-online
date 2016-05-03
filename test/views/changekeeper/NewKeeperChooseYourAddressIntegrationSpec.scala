package views.changekeeper

import composition.TestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.UiSpec
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperChooseYourAddressPage.{back, manualAddress, sadPath, happyPath}
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.VehicleLookupPage
import pages.common.ErrorPanel
import pages.common.Feedback.EmailFeedbackLink
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiTag
import webserviceclients.fakes.FakeAddressLookupService
import webserviceclients.fakes.FakeAddressLookupService.PostcodeValid
import org.scalatest.selenium.WebBrowser.{click, go, pageTitle, pageSource}

class NewKeeperChooseYourAddressIntegrationSpec extends UiSpec with TestHarness {

  final val PrivateKeeperNameLabel = " Name"
  final val BusinessKeeperNameLabel = "Business name"
  final val BusinessFleetNumberLabel = "Business fleet number"

  "new keeper choose your address page" should {
    "display the page for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(NewKeeperChooseYourAddressPage.title)
      pageSource.contains(PrivateKeeperNameLabel) should equal(true)
      pageSource.contains(BusinessKeeperNameLabel) should equal(false)
      pageSource.contains(BusinessFleetNumberLabel) should equal(false)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      pageSource.contains(EmailFeedbackLink) should equal(true)
    }

    "display the page for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(NewKeeperChooseYourAddressPage.title)
      pageSource.contains(PrivateKeeperNameLabel) should equal(false)
      pageSource.contains(BusinessKeeperNameLabel) should equal(true)
      pageSource.contains(BusinessFleetNumberLabel) should equal(true)
    }

     "redirect to vehicle lookup when no keeper cookies are in cache" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .vehicleAndKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup " +
      "when cookies are in cache for both private and business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup " +
      "when no vehicle cookies are in cache but private keeper details exist" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.privateKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup " +
      "when no vehicle cookies are in cache but business keeper details exist" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.businessKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      pageTitle should equal(VehicleLookupPage.title)
    }

    "display appropriate content " +
      "when address service returns addresses for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      pageSource.contains("No addresses found for that postcode") should equal(false) // Does not contain message
      pageSource should include( """<a id="enterAddressManuallyButton" href""")
    }

    "display appropriate content " +
      "when address service returns addresses for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      pageSource.contains("No addresses found for that postcode") should equal(false) // Does not contain message
      pageSource should include( """<a id="enterAddressManuallyButton" href""")
    }

    "display the postcode entered in the previous page for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      pageSource.contains(FakeAddressLookupService.PostcodeValid.toUpperCase) should equal(true)
    }

    "display the postcode entered in the previous page for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      pageSource.contains(FakeAddressLookupService.PostcodeValid.toUpperCase) should equal(true)
    }

    "display expected addresses in dropdown " +
      "when address service returns addresses for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage

      // The first option is the "Please select..." and the other options are the addresses.
//      NewKeeperChooseYourAddressPage.getListCount should equal(4)
      pageSource should include(
        s"presentationProperty stub, 123, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      pageSource should include(
        s"presentationProperty stub, 456, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      pageSource should include(
        s"presentationProperty stub, 789, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
    }

    "display expected addresses in dropdown " +
      "when address service returns addresses for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage

      // The first option is the "Please select..." and the other options are the addresses.
//      NewKeeperChooseYourAddressPage.getListCount should equal(4)
      pageSource should include(
        s"presentationProperty stub, 123, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      pageSource should include(
        s"presentationProperty stub, 456, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      pageSource should include(
        s"presentationProperty stub, 789, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
    }

    "display appropriate content " +
      "when address service returns no addresses for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to PrivateKeeperDetailsPage
      PrivateKeeperDetailsPage.submitPostcodeWithoutAddresses
      pageTitle should equal(NewKeeperChooseYourAddressPage.title)
      pageSource should include("No address found for that postcode")
      pageSource should include(PrivateKeeperNameLabel)
      pageSource should not include BusinessFleetNumberLabel
      pageSource should not include BusinessKeeperNameLabel
    }

    "display appropriate content " +
      "when address service returns no addresses for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to BusinessKeeperDetailsPage
      BusinessKeeperDetailsPage.submitPostcodeWithoutAddresses
      pageTitle should equal(NewKeeperChooseYourAddressPage.title)
      pageSource should include("No address found for that postcode")
      pageSource should not include PrivateKeeperNameLabel
      pageSource should include(BusinessFleetNumberLabel)
      pageSource should include(BusinessKeeperNameLabel)
    }

    "allow navigation to manual address entry when addresses have been found" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      click on manualAddress
      pageTitle should equal(NewKeeperEnterAddressManuallyPage.title)
    }

    "allow navigation to manual address entry when no addresses have been found" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      PrivateKeeperDetailsPage.submitPostcodeWithoutAddresses
      click on manualAddress
      pageTitle should equal(NewKeeperEnterAddressManuallyPage.title)
    }

    "contain the hidden csrfToken field for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper()
      go to NewKeeperChooseYourAddressPage

      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }

    "contain the hidden csrfToken field for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper()
      go to NewKeeperChooseYourAddressPage

      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should
        equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").nonEmpty should equal(true)
    }
  }

  "back button" should {
    "display private keeper details page when private keeper cookie is in cache" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      click on back
      pageTitle should equal(PrivateKeeperDetailsPage.title)
    }

    "display business keeper details page when business keeper cookie is in cache" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      click on back
      pageTitle should equal(BusinessKeeperDetailsPage.title)
    }
  }

  "select button" should {
    "go to the next page when correct data is entered for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      happyPath
      pageTitle should equal("Sale details")
    }

    "go to the next page when correct data is entered for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      happyPath
      pageTitle should equal("Sale details")
    }

    "display validation error messages " +
      "when addressSelected is not in the list for a new private keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      sadPath
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error messages " +
      "when addressSelected is not in the list for a new business keeper" taggedAs UiTag in new WebBrowserForSelenium {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      sadPath
      ErrorPanel.numberOfErrors should equal(1)
    }
  }

  private def cacheSetupPrivateKeeper()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .privateKeeperDetails()

  private def cacheSetupBusinessKeeper()(implicit webDriver: WebDriver) =
    CookieFactoryForUISpecs
      .vehicleAndKeeperDetails()
      .businessKeeperDetails()
}
