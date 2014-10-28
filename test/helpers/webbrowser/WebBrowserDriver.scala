package helpers.webbrowser

// Needed by the picocontainer
import org.openqa.selenium.support.events.EventFiringWebDriver

class WebBrowserDriver() extends EventFiringWebDriver(WebDriverFactory.webDriver)
