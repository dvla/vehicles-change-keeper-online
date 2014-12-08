package email

import models.NewKeeperDetailsViewModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel

/**
 * The email message builder class will create the contents of the message. override the buildHtml and buildText
 * with new html and text templates respectively.
 *
 * Created by gerasimosarvanitis on 04/12/2014.
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
        |<H1>${keeperDetails.displayName}</H1>
      """.stripMargin

  private def buildText(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel): String =
    s"""
        |${keeperDetails.displayName}
      """.stripMargin

}
