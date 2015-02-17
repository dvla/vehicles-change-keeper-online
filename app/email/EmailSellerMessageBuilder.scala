package email

import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

/**
 * The email message builder class will create the contents of the message. override the buildHtml and buildText
 * with new html and text templates respectively.
 *
 */
object EmailSellerMessageBuilder {
  import uk.gov.dvla.vehicles.presentation.common.services.SEND.Contents

  def buildWith(vehicleDetails: VehicleAndKeeperDetailsModel): Contents =
    Contents (
      buildHtml(vehicleDetails),
      buildText(vehicleDetails)
    )

  private def buildHtml(vehicleDetails: VehicleAndKeeperDetailsModel): String =
    s"""
        |<html>
        |<head>
        |</head>
        |<style>
        |p {
        |  line-height: 200%;
        |}
        |</style>
        |</head>
        |<body>
        |<p><b>THIS IS AN AUTOMATED EMAIL - PLEASE DO NOT REPLY.</b></p>
        |<p>Dear Sir / Madam</p>
        |
        |<p>Thank you for telling us you are no longer the registered keeper of this vehicle.
        |<br />
        |You should receive a postal acknowledgement letter within 4 weeks.
        |<br />
        |If you do not receive it within this time or this information is incorrect then please contact DVLA on
        |<a href="callto:03007906802">0300 790 6802</a>
        |<br />
        |For more information on driving and transport go to <a href="https://www.gov.uk/browse/driving">
        |https://www.gov.uk/browse/driving</a>
        |</p>
        |
        |<p>Thank You</p>
        |</body>
        |</html>
      """.stripMargin

  private def buildText(vehicleDetails: VehicleAndKeeperDetailsModel): String =
    s"""
        |THIS IS AN AUTOMATED EMAIL - PLEASE DO NOT REPLY.
        |
        |
        |Dear Sir / Madam
        |
        |Thank you for telling us you are no longer the registered keeper of this vehicle.
        |
        |You should receive a postal acknowledgement letter within 4 weeks.
        |
        |If you do not receive it within this time or this information is incorrect then please contact DVLA on  0300 790 6802
        |
        |For more information on driving and transport go to https://www.gov.uk/browse/driving
        |
        |Thank You
      """.stripMargin
}
