package uk.gov.dvla.vehicles.changekeeper.gatling

import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.http.Predef._
import Headers.{headers_accept_html, headers_accept_png, headers_x_www_form_urlencoded}

class Chains(data: RecordSeqFeederBuilder[String]) {

  private final val BeforeYouStartPageTitle = "Private sale of a vehicle"
  private final val VehicleLookupPageTitle = "Details of the vehicle being sold"
  private final val BusinessKeeperDetailsPageTitle = "Enter the details of the business buying the vehicle"
  private final val NewKeeperChooseYourAddressPageTitle = "Select the address of the buyer"
  private final val CompleteAndConfirmPageTitle = "Complete and confirm"
  private final val VehicleDetailsPlaybackHeading = "Vehicle details"
  private final val XmlEncodedApostrophe = "&#x27;"
  private final val BuyersDetailsPlaybackHeading = s"Buyer${XmlEncodedApostrophe}s Details" // TODO change the capitalisation in the app (D -> d)
  private final val SummaryPageTitle = "Summary"
  private final val TransactionDetailsPlaybackHeading = "Transaction details"

  def verifyAssetsAreAccessible =
    exec(http("screen.min.css")
      .get(s"/assets/screen.min.css")
    )
      .exec(http("print.min.css")
      .get(s"/assets/print.min.css")
      )
      .exec(
        http("govuk-crest.png")
          .get(s"/assets/lib/vehicles-presentation-common/images/govuk-crest.png")
          .headers(headers_accept_png)
      )
      .exec(
        http("govuk-crest.png")
          .get(s"/assets/lib/vehicles-presentation-common/images/govuk-crest-2x.png")
          .headers(headers_accept_png)
      )
      .exec(
        http("govuk-crest.png")
          .get(s"/assets/lib/vehicles-presentation-common/images/gov.uk_logotype_crown.png")
          .headers(headers_accept_png)
      )
      .exec(
        http("govuk-crest.png")
          .get(s"/assets/lib/vehicles-presentation-common/images/open-government-licence_2x.png")
          .headers(headers_accept_png)
      )
      .exec(http("require.js")
        .get(s"/assets/javascripts/require.js")
        .headers(Map("""Accept""" -> """*/*"""))
      )
      .exec(http("custom.js")
        .get(s"/assets/javascripts/main.js")
        .headers(Map("""Accept""" -> """*/*"""))
      )

  def beforeYouStart = {
    val chainTitle = "GET /before-you-start"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .get("/before-you-start")
          .headers(headers_accept_html)
          // Assertions
          .check(status.is(200))
          .check(regex(BeforeYouStartPageTitle).exists)
      )
    )
  }

  def vehicleLookup = {
    val chainTitle = "GET /vehicle-lookup"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .get("/vehicle-lookup")
          .headers(headers_accept_html)
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          // Assertions
          .check(regex(VehicleLookupPageTitle).exists)
      )
    )
  }

  def vehicleLookupSubmit = {
    val chainTitle = "POST /setup-trade-details"
    exitBlockOnFail(
      feed(data)
        .exec(
          http(chainTitle)
            .post("/vehicle-lookup")
            .headers(headers_x_www_form_urlencoded)
            .formParam("vehicleRegistrationNumber", "${vehicleRegistrationNumber}")
            .formParam("documentReferenceNumber", "${documentReferenceNumber}")
            .formParam("vehicleSoldTo", "${vehicleSoldTo}")
            .formParam("csrf_prevention_token", "${csrf_prevention_token}")
            .formParam("action", "")
            .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
            // Assertions
            .check(regex(BusinessKeeperDetailsPageTitle).exists)
            .check(regex(VehicleDetailsPlaybackHeading).exists)
            .check(regex("${expected_registrationNumberFormatted}").exists)
            .check(regex("${expected_make}").exists)
            .check(regex("${expected_model}").exists)
        )
    )
  }

  def businessKeeperDetailsSubmit = {
    val chainTitle = "POST /business-keeper-details"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post("/business-keeper-details")
          .headers(headers_x_www_form_urlencoded)
          .formParam("businessName", "${businessName}")
          .formParam("businesskeeper_postcode", "${businessPostcode}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          // Assertions
          .check(regex(NewKeeperChooseYourAddressPageTitle).exists)
          .check(regex(VehicleDetailsPlaybackHeading).exists)
          .check(regex("${expected_registrationNumberFormatted}").exists)
          .check(regex("${expected_make}").exists)
          .check(regex("${expected_model}").exists)
      )
    )
  }

  def newKeeperChooseYourAddressSubmit = {
    val chainTitle = "POST /new-keeper-choose-your-address"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post("/new-keeper-choose-your-address")
          .headers(headers_x_www_form_urlencoded)
          .formParam("change_keeper_newKeeperChooseYourAddress_addressSelect", "0") // UPRN disabled for Northern Ireland
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          // Assertions
          .check(regex(CompleteAndConfirmPageTitle).exists)
          .check(regex(BuyersDetailsPlaybackHeading).exists)
          .check(regex("${expected_buyerName}").exists)
          .check(regex("${expected_buyerAddressLine1}").exists)
          .check(regex("${expected_buyerAddressLine2}").exists)
          .check(regex("${expected_buyerAddressPostcode}").exists)
          .check(regex(VehicleDetailsPlaybackHeading).exists)
          .check(regex("${expected_registrationNumberFormatted}").exists)
          .check(regex("${expected_make}").exists)
          .check(regex("${expected_model}").exists)
      )
    )
  }

  def completeAndConfirmSubmit = {
    val chainTitle = "POST /complete-and-confirm"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post("/complete-and-confirm")
          .headers(headers_x_www_form_urlencoded)
          .formParam("dateofsale.day", "${dateDay}")
          .formParam("dateofsale.month", "${dateMonth}")
          .formParam("dateofsale.year", "${dateYear}")
          .formParam("consent", "${consent}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          // Assertions
          .check(regex(SummaryPageTitle).exists)
          .check(regex(TransactionDetailsPlaybackHeading).exists)
          .check(regex("${expected_transactionId}").exists)
          .check(regex("${expected_transactionDate}").exists)
      )
    )
  }
}
