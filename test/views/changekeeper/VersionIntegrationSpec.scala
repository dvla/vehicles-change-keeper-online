package views.changekeeper

import composition.TestHarness
import pages.changekeeper.VersionPage
import scala.io.Source.fromInputStream
import org.scalatest.selenium.WebBrowser.{go, pageSource}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.UiSpec

class VersionIntegrationSpec extends UiSpec with TestHarness {
  "Version endpoint" should {
    "be declared and should include the build-details.txt from classpath" in new WebBrowserForSelenium {
      go to VersionPage
      val t = fromInputStream(getClass.getResourceAsStream("/build-details.txt")).getLines().toSet.toList
      pageSource.lines.toSet.toList should contain allOf(t.head, t.tail.head, t.tail.tail.toSeq:_*)
    }
  }
}
