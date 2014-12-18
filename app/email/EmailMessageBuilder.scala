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
        |<p>Dear Sir / Madam,</p>
        |
        |<p>We have been notified that you are now the registered keeper of this vehicle.
        |<br /><br />
        |You should receive your registration certificate (V5C) within 2 weeks.
        |<br /><br />
        |If you do not receive your V5C within this time or this information is incorrect then please contact DVLA on 0300 790 6802</p>
      """.stripMargin

  private def buildText(vehicleDetails: VehicleAndKeeperDetailsModel, keeperDetails: NewKeeperDetailsViewModel): String =
    s"""
        |Dear Sir / Madam,
        |
        |We have been notified that you are now the registered keeper of this vehicle.
        |
        |You should receive your registration certificate (V5C) within 2 weeks.
        |
        |If you do not receive your V5C within this time or this information is incorrect then please contact DVLA on 0300 790 6802
      """.stripMargin

}
