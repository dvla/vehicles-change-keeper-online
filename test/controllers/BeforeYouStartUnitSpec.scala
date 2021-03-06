package controllers

import Common.PrototypeHtml
import helpers.TestWithApplication
import helpers.UnitSpec
import org.mockito.Mockito.when
import pages.changekeeper.VehicleLookupPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout, status}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config

class BeforeYouStartUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new TestWithApplication {
      val result = beforeYouStart.present(FakeRequest())
      status(result) should equal(OK)
    }

    "display prototype message when config set to true" in new TestWithApplication {
      val result = beforeYouStart.present(FakeRequest())
      contentAsString(result) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new TestWithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false) // Stub this config value.
      when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
      when(config.assetsUrl).thenReturn(None) // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()
      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "include the GA code if GA id is set" in new TestWithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.googleAnalyticsTrackingId).thenReturn(Some("TEST-GA-ID")) // Stub this config value.
      when(config.assetsUrl).thenReturn(Some("TEST-GA-ID")) // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()
      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should include(".google-analytics.com/analytics.js")
      contentAsString(result) should include("TEST-GA-ID")
    }

    "Don't include the GA code if GA id is not set" in new TestWithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config: Config = mock[Config]
      when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
      when(config.assetsUrl).thenReturn(None) // Stub this config value.
      val beforeYouStartPrototypeNotVisible = new BeforeYouStart()
      val result = beforeYouStartPrototypeNotVisible.present(request)
      contentAsString(result) should not include ".google-analytics.com/analytics.js"
    }
  }

  "submit" should {
    "redirect to next page after the button is clicked" in new TestWithApplication {
      val result = beforeYouStart.submit(FakeRequest())
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }
  }

  private lazy val beforeYouStart = injector.getInstance(classOf[BeforeYouStart])
}
