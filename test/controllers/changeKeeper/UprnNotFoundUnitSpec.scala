package controllers.changeKeeper

import composition.TestConfig
import controllers.UprnNotFound
import helpers.UnitSpec
import org.mockito.Mockito.when
import play.api.test.Helpers.{OK, contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import utils.helpers.Config
import Common.PrototypeHtml

final class UprnNotFoundUnitSpec extends UnitSpec {
  "present" should {
    "display the page" in new WithApplication {
      whenReady(present) { r =>
        r.header.status should equal(OK)
      }
    }

    "not display progress bar" in new WithApplication {
      contentAsString(present) should not include "Step "
    }

    "Use have i18n messages in the messages file" in new WithApplication {
      val content = contentAsString(present)
      content should not include "change_keeper_uprnnotfound.title"
      content should not include "change_keeper_uprnnotfound.p1"
      content should not include "change_keeper_uprnnotfound.manualaddressbutton"
      content should not include "change_keeper_uprnnotfound.setuptradedetailsbutton"
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

      val uprnNotFoundPrototypeNotVisible = new UprnNotFound()

      val result = uprnNotFoundPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }
  }

  private lazy val uprnNotFound = injector.getInstance(classOf[UprnNotFound])

  private lazy val present = {
    val request = FakeRequest()
    uprnNotFound.present(request)
  }
}