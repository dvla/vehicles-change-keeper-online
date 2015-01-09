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

  def sellToBusinessKeeper = {
    val data = csv("data/happy/SaleOfVehicleToNewBusinessKeeper.csv").circular
    val chain = new Chains(data)
    scenario("Sale of a vehicle to a new business keeper from start to finish")
      .exitBlockOnFail(
        exec(
          chain.beforeYouStart,
          chain.vehicleLookup,
          chain.vehicleLookupSubmitNewBusinessKeeper,
          chain.businessKeeperDetailsSubmit,
          chain.newKeeperChooseYourAddressSubmit,
          chain.completeAndConfirmSubmit
        )
      )
  }

  def sellToPrivateKeeper = {
    val data = csv("data/happy/SaleOfVehicleToNewPrivateKeeper.csv").circular
    val chain = new Chains(data)
    scenario("Sale of a vehicle to a new private keeper from start to finish")
      .exitBlockOnFail(
        exec(
          chain.beforeYouStart,
          chain.vehicleLookup,
          chain.vehicleLookupSubmitNewPrivateKeeper,
          chain.privateKeeperDetailsSubmit,
          chain.newKeeperChooseYourAddressSubmit//,
//          chain.completeAndConfirmSubmit
        )
      )
  }
}
