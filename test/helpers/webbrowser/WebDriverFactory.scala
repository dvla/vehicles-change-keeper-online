package helpers.webbrowser

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxProfile}
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

import java.util.concurrent.TimeUnit

object WebDriverFactory {
  private val systemProperties = System.getProperties

  def browserType = getProperty("browser.type", "htmlunit")

  def webDriver: WebDriver = {
    val targetBrowser = browserType
    webDriver(
      targetBrowser = targetBrowser,
      javascriptEnabled = false // Default to off.
    )
  }

  def webDriver(targetBrowser: String, javascriptEnabled: Boolean): WebDriver = {
    val selectedDriver: WebDriver = {

      targetBrowser match {
        case "chrome" => chromeDriver
        case "ie" => new InternetExplorerDriver()
        case "internetexplorer" => new InternetExplorerDriver()
        case "safari" => new SafariDriver()
        case "firefox" => firefoxDriver
        case "phantomjs" => phantomjsDriver(javascriptEnabled)
        case _ => htmlUnitDriver(javascriptEnabled) // Default
      }
    }

    val implicitlyWait = getProperty("browser.implicitlyWait", 5000)
    selectedDriver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.MILLISECONDS)
    selectedDriver
  }

  def webDriver(javascriptEnabled: Boolean): WebDriver = {
    webDriver(browserType, javascriptEnabled)
  }

  def testRemote: Boolean = {
    val defaultRemote = sys.env.get("test.remote") getOrElse
      sys.props.get("test.remote").getOrElse("false")
    getProperty("test.remote", default = defaultRemote.toBoolean)
  }

  def testUrl: String = {
    if (testRemote) {
      val testUrlEnvVar = sys.env.get("test.url") getOrElse
        sys.props.get("test.url").getOrElse("http://localhost:9000/")
      val testUrl = getProperty("test.url", testUrlEnvVar)
      Logger.info(s"WebDriver remote testUrl: $testUrl")
      testUrl
    }
    else {
      // Default if testing locally
      Logger.info(s"WebDriver local testUrl: http://localhost:9004")
      new String("http://localhost:9004/")
    }
  }

  private def chromeDriver = {
    systemProperties.setProperty(
      "webdriver.chrome.driver",
      getProperty("webdriver.chrome.driver", s"test/resources/drivers/chromedriver-2.9_$driverSuffix"))
    new ChromeDriver()
  }

  private def htmlUnitDriver(javascriptEnabled: Boolean) = {
    val driver = new HtmlUnitDriver()
    driver.setJavascriptEnabled(javascriptEnabled) // TODO HTMLUnit blows up when navigating live site due to JavaScript errors!
    driver
  }

  private def firefoxDriver = {
    val firefoxProfile = new FirefoxProfile()
    firefoxProfile.setAcceptUntrustedCertificates(true)
    new FirefoxDriver(firefoxProfile)
  }

  private def phantomjsDriver(javascriptEnabled: Boolean) = {
    systemProperties.setProperty(
      "webdriver.phantomjs.binary",
      getProperty("webdriver.phantomjs.binary", s"test/resources/drivers/phantomjs-1.9.7_$driverSuffix")
    )

    val capabilities = new DesiredCapabilities
    capabilities.setJavascriptEnabled(javascriptEnabled)
    capabilities.setCapability("takesScreenshot", false)
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
      systemProperties.getProperty("webdriver.phantomjs.binary")
    )
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
      Array("--ignore-ssl-errors=yes", "--web-security=false", "--ssl-protocol=any")
    )

    new PhantomJSDriver(capabilities)
  }

  private val driverSuffix: String = sys.props.get("os.name") match {
    case Some(os) if os.contains("mac") => "macosx"
    case _ => "linux64"
  }
}