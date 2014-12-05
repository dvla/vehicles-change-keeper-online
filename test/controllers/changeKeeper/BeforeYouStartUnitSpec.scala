package controllers.changeKeeper

import composition.WithChangeKeeperApplication
import controllers.BeforeYouStart
import controllers.changeKeeper.Common.PrototypeHtml
import helpers.UnitSpec
import org.mockito.Mockito.when
import pages.changekeeper.VehicleLookupPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout, status}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config

class BeforeYouStartUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithChangeKeeperApplication {
      val result = beforeYouStart.present(FakeRequest())
      status(result) should equal(OK)
    }

    "display prototype message when config set to true" in new WithChangeKeeperApplication {
      val result = beforeYouStart.present(FakeRequest())
      contentAsString(result) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithChangeKeeperApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false) // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()

      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "include the GA code if GA id is set" in new WithChangeKeeperApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.googleAnalyticsTrackingId).thenReturn("TEST-GA-ID") // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()

      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should include("GoogleAnalyticsObject")
      contentAsString(result) should include("TEST-GA-ID")
    }

    "Don't include the GA code if GA id is not set" in new WithChangeKeeperApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.googleAnalyticsTrackingId).thenReturn("NOT FOUND") // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()

      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should not include "GoogleAnalyticsObject"
    }
  }

  "submit" should {
    "redirect to next page after the button is clicked" in new WithChangeKeeperApplication {
      val result = beforeYouStart.submit(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }
  }

  private val beforeYouStart = injector.getInstance(classOf[BeforeYouStart])
}
