package gov.uk.dvla.vehicles.acquire.accepancetest

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.Ignore
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@Ignore
@CucumberOptions(
  features = Array("acceptance-tests/src/test/resources/gherkin/US1626.feature"),
  glue = Array("gov.uk.dvla.vehicles.acquire.stepdefs"),
  tags = Array("@tag")
)
class US1626AcceptanceTest
