package uk.gov.dvla.vehicles.changekeeper.gatling

import io.gatling.core.Predef._
import io.gatling.core.feeder.{Record, RecordSeqFeederBuilder}

object Scenarios {

  def verifyAssetsAreAccessible = {
    val noData = RecordSeqFeederBuilder[String](records = IndexedSeq.empty[Record[String]])
    val chain = new Chains(noData)
    scenario("Verify assets are accessible")
      .exec(
        chain.verifyAssetsAreAccessible
      )
  }

  def endToEnd = {
    val data = csv("data/happy/EndToEnd.csv").circular
    val chain = new Chains(data)
    scenario("Single disposal of a vehicle to a new private keeper from start to finish")
      .exitBlockOnFail(
        exec(
          chain.beforeYouStart,
          chain.vehicleLookup,
          chain.vehicleLookupSubmit,
          chain.businessKeeperDetailsSubmit,
          chain.newKeeperChooseYourAddressSubmit,
          chain.completeAndConfirmSubmit
        )
      )
  }
}
