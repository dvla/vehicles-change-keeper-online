package controllers.changeKeeper

import controllers.Help
import controllers.changeKeeper.Common.PrototypeHtml
import helpers.common.CookieHelper
import CookieHelper.{fetchCookiesFromHeaders, verifyCookieHasBeenDiscarded}
import helpers.changekeeper.CookieFactoryForUnitSpecs
import helpers.{UnitSpec, WithApplication}
import models.HelpCacheKey
import org.mockito.Mockito.when
import pages.changekeeper.BeforeYouStartPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, OK, REFERER, contentAsString, defaultAwaitTimeout, status}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config

final class HelpUnitSpec extends UnitSpec {
  "present" should {
    "display the help page" in new WithApplication {
      status(present) should equal(OK)
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
      when(config.isPrototypeBannerVisible).thenReturn(false)
      // Stub this config value.
      val helpPrototypeNotVisible = new Help()

      val result = helpPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    // FIXME: FIX THIS WHEN THE STORY IS READY
    "write help cookie" ignore new WithApplication {
      /*
      val origin = SetupTradeDetailsPage.address
      val request = FakeRequest().
        withHeaders(REFERER -> origin)
      // Set the previous page.
      val result = help.present(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.find(_.name == HelpCacheKey).get.value should equal(origin)
      }
      */
    }
  }

  "back" should {
    "redirect to first page when there is no referer" in new WithApplication {
      val request = FakeRequest()
      // No previous page cookie, which can only happen if they wiped their cookies after
      // page presented or they are calling the route directly.
      val result = help.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    // FIXME: FIX THIS WHEN THE STORY IS READY
    "redirect to previous page and discard the referer cookie" ignore new WithApplication {
/*
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.help(origin = SetupTradeDetailsPage.address))
      val result = help.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(SetupTradeDetailsPage.address))
        val cookies = fetchCookiesFromHeaders(r)
        verifyCookieHasBeenDiscarded(HelpCacheKey, cookies)
      }
*/
    }
  }

  private val help = injector.getInstance(classOf[Help])
  private lazy val present = help.present(FakeRequest())
}