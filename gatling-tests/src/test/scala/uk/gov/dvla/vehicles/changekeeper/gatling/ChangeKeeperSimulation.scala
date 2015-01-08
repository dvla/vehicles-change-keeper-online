package uk.gov.dvla.vehicles.changekeeper.gatling

import Helper.httpConf
import io.gatling.core.Predef._
import uk.gov.dvla.vehicles.changekeeper.gatling.Scenarios.verifyAssetsAreAccessible
import uk.gov.dvla.vehicles.changekeeper.gatling.Scenarios.endToEnd

class ChangeKeeperSimulation extends Simulation {

  private val oneUser = atOnceUsers(1)

  setUp(
    verifyAssetsAreAccessible.inject(oneUser),
    endToEnd.inject(oneUser)
  ).
    protocols(httpConf).
    assertions(global.failedRequests.count.is(0))
}
