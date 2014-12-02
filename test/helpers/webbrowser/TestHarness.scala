package helpers.webbrowser

import helpers.common.ProgressBar
import ProgressBar.{fakeApplicationWithProgressBarFalse, fakeApplicationWithProgressBarTrue}
import play.api.Logger
import play.api.test._
import org.openqa.selenium.WebDriver
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import play.api.test.TestServer
import play.api.test.FakeApplication
import org.specs2.execute.{Result, AsResult}


// NOTE: Do *not* put any initialisation code in the class below, otherwise delayedInit() gets invoked twice
// which means around() gets invoked twice and everything is not happy.  Only lazy vals and defs are allowed,
// no vals or any other code blocks.

trait TestHarness {
  import WebBrowser._
  abstract class WebBrowser(val app: FakeApplication = fakeAppWithTestGlobal,
                            val port: Int = testPort,
                            implicit protected val webDriver: WebDriver = WebDriverFactory.webDriver
                             )
      extends Around with Scope with WebBrowserDSL {

    override def around[T: AsResult](t: => T): Result = {
      configureTestUrl(port) {
        try Helpers.running(TestServer(port, app))(AsResult.effectively(t))
        finally webDriver.quit()
      }
    }

    private def configureTestUrl(port: Int)(code: => Result): Result = {
      val value = s"http://localhost:$port/"
      Logger.debug(s"TestHarness - Set system property ${TestConfiguration.TestUrl} to value $value")
      sys.props += ((TestConfiguration.TestUrl, value))
      try code
      finally sys.props -= TestConfiguration.TestUrl
    }
  }

  abstract class ProgressBarTrue extends WebBrowser(app = fakeApplicationWithProgressBarTrue)
  abstract class ProgressBarFalse extends WebBrowser(app = fakeApplicationWithProgressBarFalse)

  abstract class WebBrowserWithJs extends WebBrowser(
    webDriver = WebDriverFactory.webDriver(javascriptEnabled = true)
  )

  object WebBrowser {
    private lazy val fakeAppWithTestGlobal: FakeApplication = FakeApplication(withGlobal = Some(TestGlobal))
    private lazy val testPort: Int = TestConfiguration.testPort
  }
}