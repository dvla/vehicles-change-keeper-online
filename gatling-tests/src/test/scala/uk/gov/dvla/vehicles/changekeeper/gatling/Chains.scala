package uk.gov.dvla.vehicles.changekeeper.gatling

import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.http.Predef._
import Headers.{headers_accept_html, headers_accept_png, headers_x_www_form_urlencoded}

class Chains(data: RecordSeqFeederBuilder[String]) {

  private final val BeforeYouStartPageTitle = "Private sale of a vehicle"
  private final val VehicleLookupPageTitle = "Details of the vehicle being sold"
  private final val VehicleLookupFailurePageTitle = "Unable to find a vehicle record"
  private final val BusinessKeeperDetailsPageTitle = "Enter new keeper details"
  private final val PrivateKeeperDetailsPageTitle = "Enter new keeper details"
  private final val NewKeeperChooseYourAddressPageTitle = "Select new keeper address"
  private final val DateOfSalePageTitle = "Date of sale"
  private final val CompleteAndConfirmPageTitle = "Complete and confirm"
  private final val VehicleDetailsPlaybackHeading = "Vehicle details"
  private final val BuyersDetailsPlaybackHeading = s"New keeper details"
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
    val url = "/before-you-start"
    val chainTitle = s"GET $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .get(url)
          .headers(headers_accept_html)
          // Assertions
          .check(status.is(200))
          .check(regex(BeforeYouStartPageTitle).exists)
      )
    )
  }

  def vehicleLookup = {
    val url = "/vehicle-lookup"
    val chainTitle = s"GET $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .get(url)
          .headers(headers_accept_html)
          // Assertions
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          .check(regex(VehicleLookupPageTitle).exists)
      )
    )
  }

  def vehicleLookupSubmitNewBusinessKeeper = vehicleLookupSubmit(BusinessKeeperDetailsPageTitle)
  def vehicleLookupSubmitNewPrivateKeeper = vehicleLookupSubmit(PrivateKeeperDetailsPageTitle)

  private def vehicleLookupSubmit(expectedPageTitle: String) = {
    val url = "/vehicle-lookup"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      feed(data)
        .exec(
          http(chainTitle)
            .post(url)
            .headers(headers_x_www_form_urlencoded)
            .formParam("vehicleRegistrationNumber", "${vehicleRegistrationNumber}")
            .formParam("documentReferenceNumber", "${documentReferenceNumber}")
            .formParam("vehicleSoldTo", "${vehicleSoldTo}")
            .formParam("vehicleSellerEmailOption", "invisible")
            .formParam("csrf_prevention_token", "${csrf_prevention_token}")
            .formParam("action", "")
            // Assertions
            .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
            .check(regex(expectedPageTitle).exists)
            .check(regex(VehicleDetailsPlaybackHeading).exists)
            .check(regex("${expected_registrationNumberFormatted}").exists)
            .check(regex("${expected_make}").exists)
            .check(regex("${expected_model}").exists)
        )
    )
  }

  def businessKeeperDetailsSubmit = {
    val url = "/business-keeper-details"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post(url)
          .headers(headers_x_www_form_urlencoded)
          .formParam("fleetNumberOption", "invisible")
          .formParam("fleetNumber", "${fleetNumber}")
          .formParam("businessName", "${businessName}")
          .formParam("businesskeeper_option_email", "invisible")
          .formParam("businessKeeper_email", "${businessEmail}")
          .formParam("businesskeeper_postcode", "${businessPostcode}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          // Assertions
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          .check(regex(NewKeeperChooseYourAddressPageTitle).exists)
          .check(regex(VehicleDetailsPlaybackHeading).exists)
          .check(regex("${expected_registrationNumberFormatted}").exists)
          .check(regex("${expected_make}").exists)
          .check(regex("${expected_model}").exists)
      )
    )
  }

  def privateKeeperDetailsSubmit = {
    val url = "/private-keeper-details"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post(url)
          .headers(headers_x_www_form_urlencoded)
          .formParam("privatekeeper_title.titleOption", "${privateKeeperTitle}")
          .formParam("privatekeeper_title.titleText", "${privateKeeperTitleText}")
          .formParam("privatekeeper_firstname", "${privateKeeperFirstName}")
          .formParam("privatekeeper_lastname", "${privateKeeperLastName}")
          .formParam("privatekeeper_dateofbirth.day", "${dateOfBirthDay}")
          .formParam("privatekeeper_dateofbirth.month", "${dateOfBirthMonth}")
          .formParam("privatekeeper_dateofbirth.year", "${dateOfBirthYear}")
          .formParam("privatekeeper_drivernumber", "${driverNumber}")
          .formParam("privatekeeper_option_email", "invisible")
          .formParam("privatekeeper_email", "${email}")
          .formParam("privatekeeper_postcode", "${privateKeeperPostcode}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          // Assertions
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          .check(regex(NewKeeperChooseYourAddressPageTitle).exists)
          .check(regex(VehicleDetailsPlaybackHeading).exists)
          .check(regex("${expected_registrationNumberFormatted}").exists)
          .check(regex("${expected_make}").exists)
          .check(regex("${expected_model}").exists)
      )
    )
  }

  def newKeeperChooseYourAddressSubmit = {
    val url = "/new-keeper-choose-your-address"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post(url)
          .headers(headers_x_www_form_urlencoded)
          .formParam("newKeeperChooseYourAddress_addressSelect", "0") // UPRN disabled for Northern Ireland
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          // Assertions
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
//          .check(regex(DateOfSalePageTitle).exists)
//          .check(regex(BuyersDetailsPlaybackHeading).exists)
//          .check(regex("${expected_buyerName}").exists)
//          .check(regex("${expected_buyerAddressLine1}").exists)
//          .check(regex("${expected_buyerAddressLine2}").exists)
//          .check(regex("${expected_buyerAddressPostcode}").exists)
//          .check(regex(VehicleDetailsPlaybackHeading).exists)
//          .check(regex("${expected_registrationNumberFormatted}").exists)
//          .check(regex("${expected_make}").exists)
//          .check(regex("${expected_model}").exists)
      )
    )
  }

  def dateOfSaleSubmit = {
    val url = "/date-of-sale"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post(url)
          .headers(headers_x_www_form_urlencoded)
          .formParam("mileage", "${mileage}")
          .formParam("dateofsale.day", "${dateDay}")
          .formParam("dateofsale.month", "${dateMonth}")
          .formParam("dateofsale.year", "${dateYear}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          // Assertions
          .check(regex( """<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
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
    val url = "/complete-and-confirm"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      exec(
        http(chainTitle)
          .post(url)
          .headers(headers_x_www_form_urlencoded)
          .formParam("regRight", "${consent}")
          .formParam("consent", "${consent}")
          .formParam("csrf_prevention_token", "${csrf_prevention_token}")
          .formParam("action", "")
          // Assertions
          .check(regex("""<input type="hidden" name="csrf_prevention_token" value="(.*)"/>""").saveAs("csrf_prevention_token"))
          .check(regex(SummaryPageTitle).exists)
          .check(regex(TransactionDetailsPlaybackHeading).exists)
          .check(regex("${expected_transactionId}").exists)
          .check(regex("${expected_transactionDate}").exists)
          .notSilent
      )
    )
  }

  def vehicleLookupUnsuccessfulSubmit = {
    val url = "/vehicle-lookup"
    val chainTitle = s"POST $url"
    exitBlockOnFail(
      feed(data)
        .exec(
          http(chainTitle)
            .post(url)
            .headers(headers_x_www_form_urlencoded)
            .formParam("vehicleRegistrationNumber", "${vehicleRegistrationNumber}")
            .formParam("documentReferenceNumber", "${documentReferenceNumber}")
            .formParam("vehicleSellerEmailOption", "invisible")
            .formParam("vehicleSoldTo", "${vehicleSoldTo}")
            .formParam("csrf_prevention_token", "${csrf_prevention_token}")
            .formParam("action", "")
            // Assertions
            .check(regex(VehicleLookupFailurePageTitle).exists)
        )
    )
  }
}
