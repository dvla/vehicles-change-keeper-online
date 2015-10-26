package controllers

import Common.PrototypeHtml
import composition.WithApplication
import helpers.{UnitSpec}
import org.mockito.Mockito.when
import pages.changekeeper.{BeforeYouStartPage, VehicleLookupPage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, SERVICE_UNAVAILABLE, status, LOCATION, REFERER}
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieFactoryForUnitSpecs
import utils.helpers.Config
import MicroServiceError.MicroServiceErrorRefererCacheKey
import common.testhelpers.CookieHelper.fetchCookiesFromHeaders

class MicroServiceErrorUnitSpec extends UnitSpec  {
  "present" should {
    "display the page" in new WithApplication {
      status(present) should equal(SERVICE_UNAVAILABLE)
    }

    "not display progress bar" in new WithApplication {
      contentAsString(present) should not include "Step "
    }

    "display prototype message when config set to true" in new WithApplication {
      contentAsString(present) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false) // Stub this config value.
      when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
      when(config.assetsUrl).thenReturn(None) // Stub this config value.
      val microServiceErrorPrototypeNotVisible = new MicroServiceError()

      val result = microServiceErrorPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "write micro service error referer cookie" ignore new WithApplication {
      val referer = VehicleLookupPage.address
      val request = FakeRequest()
        .withHeaders(REFERER -> referer)
      // Set the previous page.
      val result = microServiceError.present(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.find(_.name == MicroServiceErrorRefererCacheKey).get.value should equal(referer)
      }
    }
  }

  "try again" should {
    "redirect to vehicle lookup page when there is no referer" in new WithApplication {
      val request = FakeRequest()
      // No previous page cookie, which can only happen if they wiped their cookies after
      // page presented or they are calling the route directly.
      val result = microServiceError.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "redirect to previous page and discard the referer cookie" ignore new WithApplication {
//      val request = FakeRequest()
//        .withCookies(CookieFactoryForUnitSpecs.microServiceError(VehicleLookupPage.address))
//      val result = microServiceError.back(request)
//      whenReady(result) { r =>
//        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
//        val cookies = fetchCookiesFromHeaders(r)
//        verifyCookieHasBeenDiscarded(MicroServiceErrorRefererCacheKey, cookies)
//      }
    }
  }

  private lazy val microServiceError = injector.getInstance(classOf[MicroServiceError])
  private lazy val present = microServiceError.present(FakeRequest())
}
