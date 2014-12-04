package views.changekeeper

import composition.ChangeKeeperTestHarness
import helpers.changekeeper.CookieFactoryForUISpecs
import helpers.tags.UiTag
import helpers.webbrowser.ProgressBar
import helpers.UiSpec
import org.openqa.selenium.{By, WebElement, WebDriver}
import pages.common.ErrorPanel
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BusinessKeeperDetailsPage
import pages.changekeeper.PrivateKeeperDetailsPage
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import pages.changekeeper.VehicleLookupPage
import pages.changekeeper.NewKeeperChooseYourAddressPage.{back, manualAddress, sadPath, happyPath}
import ProgressBar.progressStep
import uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction
import webserviceclients.fakes.FakeAddressLookupService
import webserviceclients.fakes.FakeAddressLookupService.PostcodeValid
import pages.common.Feedback.EmailFeedbackLink

class NewKeeperChooseYourAddressIntegrationSpec extends UiSpec with ChangeKeeperTestHarness {
  final val ProgressStepNumber = 4
  final val PrivateKeeperNameLabel = " Name"
  final val BusinessKeeperNameLabel = "Business name"
  final val BusinessFleetNumberLabel = "Business fleet number"

  "new keeper choose your address page" should {
    "display the page for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.title should equal(NewKeeperChooseYourAddressPage.title)
      page.source.contains(PrivateKeeperNameLabel) should equal(true)
      page.source.contains(BusinessKeeperNameLabel) should equal(false)
      page.source.contains(BusinessFleetNumberLabel) should equal(false)
    }

    "contain feedback email facility with appropriate subject" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains(EmailFeedbackLink) should equal(true)
    }

    "display the page for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      page.title should equal(NewKeeperChooseYourAddressPage.title)
      page.source.contains(PrivateKeeperNameLabel) should equal(false)
      page.source.contains(BusinessKeeperNameLabel) should equal(true)
      page.source.contains(BusinessFleetNumberLabel) should equal(true)
    }

    "display the progress of the page when progressBar is set to true" taggedAs UiTag in new ProgressBarTrue {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(true)
    }

    "not display the progress of the page when progressBar is set to false" taggedAs UiTag in new ProgressBarFalse {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains(progressStep(ProgressStepNumber)) should equal(false)
    }

    "redirect to vehicle lookup when no keeper cookies are in cache" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs
        .vehicleAndKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      page.title should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup when cookies are in cache for both private and business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      page.title should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup when no vehicle cookies are in cache but private keeper details exist" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.privateKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      page.title should equal(VehicleLookupPage.title)
    }

    "redirect to vehicle lookup when no vehicle cookies are in cache but business keeper details exist" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.businessKeeperDetails()
      go to NewKeeperChooseYourAddressPage
      page.title should equal(VehicleLookupPage.title)
    }

    "display appropriate content when address service returns addresses for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains("No addresses found for that postcode") should equal(false) // Does not contain message
      page.source should include( """<a id="enterAddressManuallyButton" href""")
    }

    "display appropriate content when address service returns addresses for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains("No addresses found for that postcode") should equal(false) // Does not contain message
      page.source should include( """<a id="enterAddressManuallyButton" href""")
    }

    "display the postcode entered in the previous page for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains(FakeAddressLookupService.PostcodeValid.toUpperCase) should equal(true)
    }

    "display the postcode entered in the previous page for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      page.source.contains(FakeAddressLookupService.PostcodeValid.toUpperCase) should equal(true)
    }

    "display expected addresses in dropdown when address service returns addresses for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage

      NewKeeperChooseYourAddressPage.getListCount should equal(4) // The first option is the "Please select..." and the other options are the addresses.
      page.source should include(
        s"presentationProperty stub, 123, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      page.source should include(
        s"presentationProperty stub, 456, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      page.source should include(
        s"presentationProperty stub, 789, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
    }

    "display expected addresses in dropdown when address service returns addresses for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage

      NewKeeperChooseYourAddressPage.getListCount should equal(4) // The first option is the "Please select..." and the other options are the addresses.
      page.source should include(
        s"presentationProperty stub, 123, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      page.source should include(
        s"presentationProperty stub, 456, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
      page.source should include(
        s"presentationProperty stub, 789, property stub, street stub, town stub, area stub, $PostcodeValid"
      )
    }

    "display appropriate content when address service returns no addresses for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to PrivateKeeperDetailsPage
      PrivateKeeperDetailsPage.submitPostcodeWithoutAddresses
      page.title should equal("No address found")
      page.source should include("No address found for that postcode")
      page.source should include(PrivateKeeperNameLabel)
      page.source should not include BusinessFleetNumberLabel
      page.source should not include BusinessKeeperNameLabel
    }

    "display appropriate content when address service returns no addresses for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      CookieFactoryForUISpecs.vehicleAndKeeperDetails()
      go to BusinessKeeperDetailsPage
      BusinessKeeperDetailsPage.submitPostcodeWithoutAddresses
      page.title should equal("No address found")
      page.source should include("No address found for that postcode")
      page.source should not include PrivateKeeperNameLabel
      page.source should include(BusinessFleetNumberLabel)
      page.source should include(BusinessKeeperNameLabel)
    }

    "allow navigation to manual address entry when addresses have been found" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      click on manualAddress
      page.title should equal(NewKeeperEnterAddressManuallyPage.title)
    }

    "allow navigation to manual address entry when no addresses have been found" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      PrivateKeeperDetailsPage.submitPostcodeWithoutAddresses
      click on manualAddress
      page.title should equal(NewKeeperEnterAddressManuallyPage.title)
    }

    "contain the hidden csrfToken field for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper()
      go to NewKeeperChooseYourAddressPage

      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }

    "contain the hidden csrfToken field for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper()
      go to NewKeeperChooseYourAddressPage

      val csrf: WebElement = webDriver.findElement(By.name(CsrfPreventionAction.TokenName))
      csrf.getAttribute("type") should equal("hidden")
      csrf.getAttribute("name") should equal(uk.gov.dvla.vehicles.presentation.common.filters.CsrfPreventionAction.TokenName)
      csrf.getAttribute("value").size > 0 should equal(true)
    }
  }

  "back button" should {
    "display private keeper details page when private keeper cookie is in cache" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      go to NewKeeperChooseYourAddressPage
      click on back
      page.title should equal(PrivateKeeperDetailsPage.title)
    }

    "display business keeper details page when business keeper cookie is in cache" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      go to NewKeeperChooseYourAddressPage
      click on back
      page.title should equal(BusinessKeeperDetailsPage.title)
    }
  }

  "select button" should {
    "go to the next page when correct data is entered for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      happyPath
      page.title should equal("Complete and confirm")
    }

    "go to the next page when correct data is entered for a new business keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupBusinessKeeper
      happyPath
      page.title should equal("Complete and confirm")
    }

    "display validation error messages when addressSelected is not in the list for a new private keeper" taggedAs UiTag in new WebBrowser {
      go to BeforeYouStartPage
      cacheSetupPrivateKeeper
      sadPath
      ErrorPanel.numberOfErrors should equal(1)
    }

    "display validation error messages when addressSelected is not in the list for a new business keeper" taggedAs UiTag in new WebBrowser {
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
