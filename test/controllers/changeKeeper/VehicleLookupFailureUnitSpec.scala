package controllers.changeKeeper

import composition.WithChangeKeeperApplication
import controllers.VehicleLookupFailure
import Common.PrototypeHtml
import helpers.UnitSpec
import helpers.changekeeper.CookieFactoryForUnitSpecs
import org.mockito.Mockito.when
import pages.changekeeper.{BeforeYouStartPage, VehicleLookupPage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config

final class VehicleLookupFailureUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithChangeKeeperApplication {
      whenReady(present) { r =>
        r.header.status should equal(OK)
      }
    }

    "redirect to before you start if bruteForcePreventionViewModel is not in cache" in new WithChangeKeeperApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupResponseCode())
      val result = vehicleLookupFailure.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "redirect to before you start if VehicleLookupFormModelCache is not in cache" in new WithChangeKeeperApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.bruteForcePreventionViewModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupResponseCode())
      val result = vehicleLookupFailure.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "redirect to before you start if only vehicleLookupResponseCode is not in cache" in new WithChangeKeeperApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.bruteForcePreventionViewModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
      val result = vehicleLookupFailure.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "not display progress bar" in new WithChangeKeeperApplication {
      contentAsString(present) should not include "Step "
    }

    "display prototype message when config set to true" in new WithChangeKeeperApplication {
      contentAsString(present) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithChangeKeeperApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false) // Stub this config value.
      val vehicleLookupFailurePrototypeNotVisible = new VehicleLookupFailure()

      val result = vehicleLookupFailurePrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }
  }

  "submit" should {
    "redirect to vehiclelookup on submit" in new WithChangeKeeperApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
      val result = vehicleLookupFailure.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to setuptraderdetails on submit when cache is empty" in new WithChangeKeeperApplication {
      val request = FakeRequest()
      val result = vehicleLookupFailure.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }
  }

  private val vehicleLookupFailure = {
    injector.getInstance(classOf[VehicleLookupFailure])
  }

  private lazy val present = {
    val request = FakeRequest()
      .withCookies(CookieFactoryForUnitSpecs.bruteForcePreventionViewModel())
      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupResponseCode())
    vehicleLookupFailure.present(request)
  }
}
