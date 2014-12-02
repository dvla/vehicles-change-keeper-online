package helpers.webbrowser

import org.specs2.execute.Result
import play.api.Logger

object TestConfiguration {
  private final val TestUrl = "test.url"
  private final val TestPort = "test.port"
  private final val DefaultTestPort = "9001"

  def testUrl: String = {
    Logger.debug(s"testUrl - Looking in for property $TestUrl in system props and environment vars...")
    val sysOrEnvProp = sys.props.get(TestUrl)
      .orElse(sys.env.get(environmentVariableName(TestUrl)))
      .getOrElse(throw new RuntimeException(s"Error - cannot run tests. You need to configure property <$TestUrl>"))
    Logger.debug(s"testUrl - Found property $TestUrl in system or environment properties, value = $sysOrEnvProp")
    sysOrEnvProp
  }

  def testPort: Int = sys.props.get(TestPort)
      .orElse(sys.env.get(environmentVariableName(TestPort)))
      .getOrElse(DefaultTestPort).toInt

  def configureTestUrl(port: Int = testPort)(code: => Result): Result = {
    val value = s"http://localhost:$port/"
    Logger.debug(s"TestHarness - Set system property ${TestUrl} to value $value")
    sys.props += ((TestUrl, value))
    try code
    finally sys.props -= TestUrl
  }

  // The environment variables have underscore instead of full stop
  private def environmentVariableName(systemProperty: String) : String = systemProperty.replace('.', '_')
}
