package email

import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperDetailsViewModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.services.SEND

/**
 * The email message builder class will create the contents of the message. override the buildHtml and buildText
 * with new html and text templates respectively.
 *
 */
object EmailMessageBuilder {
  import SEND.Contents

  def buildWith(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel): Contents =
    Contents (
      buildHtml(vehicleDetails, keeperDetails),
      buildText(vehicleDetails, keeperDetails)
    )

  private def buildHtml(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel): String =
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
         |<p>We have been notified that you are now the registered keeper of this vehicle.</p>
         |
         |<p>
         |Since 1st October 2014, the vehicle tax can no longer be transferred as part of the sale.
         |This is because the seller will automatically receive a refund of any remaining tax.
         |</p>
         |<p>
         |<b>
         |
         |<p>You must tax this vehicle before it is driven on the road.
         |<br />
         |<a href="https://www.gov.uk/vehicle-tax">Tax now?</a>
         |</p>
         |
         |<p>If you do not want to tax you can make a SORN declaration.
         |<br />
         |<a href="https://www.gov.uk/make-a-sorn">SORN now ?</a>
         |</p>
         |
         |</b>
         |</p>
         |<p>
         |You should receive your registration certificate (V5C) within 2 weeks.
         |If you do not receive your V5C within this time or this information is incorrect then please contact DVLA on
         |
         |<a href="callto:03007906802">0300 790 6802</a>
         |</p>
         |<p>
      """.stripMargin

  private def buildText(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel): String =
    s"""
       |THIS IS AN AUTOMATED EMAIL - PLEASE DO NOT REPLY.
       |
       |
       |Dear Sir / Madam
       |We have been notified that you are now the registered keeper of this vehicle.
       |
       |Since 1st October 2014, the vehicle tax can no longer be transferred as part of the sale.
       |This is because the seller will automatically receive a refund of any remaining tax.
       |
       |You must tax this vehicle before it is driven on the road.
       |Tax now? Go to https://www.gov.uk/vehicle-tax
       |
       |If you do not want to tax you can make a SORN declaration.
       |SORN now? Go to https://www.gov.uk/make-a-sorn
       |
       |You should receive your registration certificate (V5C) within 2 weeks. If you do not receive your V5C within
       |this time or this information is incorrect then please contact DVLA on 0300 790 6802
       |
       |If you do not receive your V5C within this time or this information is incorrect then please contact DVLA on 0300 790 6802
       |
       |For more information on driving and transport go to https://www.gov.uk/browse/driving
       |
       |Thank You
      """.stripMargin

}
