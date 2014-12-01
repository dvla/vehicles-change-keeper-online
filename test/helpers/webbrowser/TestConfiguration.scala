package helpers.webbrowser

object TestConfiguration {
  private final val TestUrl = "test.url"
  private final val TestPort = "test.port"
  private final val DefaultTestPort = "9001"

  def testUrl: String = {
    println(s"testUrl - Looking in for property $TestUrl in system props and environment vars...")
    val sysOrEnvProp = sys.props.get(TestUrl)
      .orElse(sys.env.get(environmentVariableName(TestUrl)))
      .getOrElse(throw new RuntimeException(s"Error - cannot run tests. You need to configure property <$TestUrl>"))
    println(s"testUrl - Found property $TestUrl in system or environment properties, value = $sysOrEnvProp")
    sysOrEnvProp
  }

  def testPort: Int = {
    val sysOrEnvProp = sys.props.get(TestPort)
      .orElse(sys.env.get(environmentVariableName(TestPort)))
      .getOrElse(DefaultTestPort)
    println(s"testPort - $TestPort from system or environment properties or default value, value = $sysOrEnvProp")
    val value = s"http://localhost:$sysOrEnvProp/"
    sys.props += ((TestUrl, value))
    println(s"testPort - Set system property $TestUrl to value $value")
    sysOrEnvProp.toInt
  }

  // The environment variables have underscore instead of full stop
  private def environmentVariableName(systemProperty: String) : String = systemProperty.replace('.', '_')
}
