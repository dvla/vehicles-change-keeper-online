package gov.uk.dvla.vehicles.acquire.accepancetest

import cucumber.api.junit.Cucumber
import cucumber.api.junit.Cucumber.Options
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@Options(
  features = Array("acceptance-tests/src/test/resources/gherkin/"),
  glue = Array("gov.uk.dvla.vehicles.acquire.stepdefs"),
  tags = Array("@tag")
)
class RunTest {
}

/*
object FeaturePath {
  final val Path = getClass.getClassLoader.getResource("/gherkin/").toURI.getPath
}
*/

