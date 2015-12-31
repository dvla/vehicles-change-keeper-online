package gov.uk.dvla.vehicles.keeper.stepdefs.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("acceptance-tests/src/test/resources/gherkin/BusinessKeeperDetails.feature",
                   "acceptance-tests/src/test/resources/gherkin/HappyAndSadPath.feature",
                   "acceptance-tests/src/test/resources/gherkin/PrivateKeeperDetails.feature"),
  glue = Array("gov.uk.dvla.vehicles.keeper.stepdefs"),
  tags = Array("@tag")
)
// These tests can cause brute force to timeout when run in parallel.
class SequentialTests
