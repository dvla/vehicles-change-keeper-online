package gov.uk.dvla.vehicles.keeper.accepancetest

import cucumber.api.junit.Cucumber
import cucumber.api.junit.Cucumber.Options
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@Options(
  features = Array("acceptance-tests/src/test/resources/gherkin/"),
  glue = Array("gov.uk.dvla.vehicles.keeper.stepdefs"),
  tags = Array("@tag")
)
class KeeperToKeeperRunnerTest {
}


