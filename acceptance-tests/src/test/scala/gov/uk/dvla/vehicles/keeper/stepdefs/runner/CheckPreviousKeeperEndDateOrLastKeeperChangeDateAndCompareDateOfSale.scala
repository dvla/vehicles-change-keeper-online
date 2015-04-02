package gov.uk.dvla.vehicles.keeper.stepdefs.runner

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("acceptance-tests/src/test/resources/gherkin/CheckPreviousKeeperEndDateOrLastKeeperChangeDateAndCompareDateOfSale.feature"),
  glue = Array("gov.uk.dvla.vehicles.keeper.stepdefs"),
  tags = Array("@tag")
)
class CheckPreviousKeeperEndDateOrLastKeeperChangeDateAndCompareDateOfSale {

}
