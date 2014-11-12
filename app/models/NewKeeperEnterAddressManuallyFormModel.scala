package models

import play.api.data.Forms.mapping
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CacheKey
import uk.gov.dvla.vehicles.presentation.common.views.models.AddressAndPostcodeViewModel

final case class NewKeeperEnterAddressManuallyFormModel(addressAndPostcodeModel: AddressAndPostcodeViewModel)

object NewKeeperEnterAddressManuallyFormModel {
  implicit val JsonFormat = Json.format[NewKeeperEnterAddressManuallyFormModel]

  final val NewKeeperEnterAddressManuallyCacheKey = "newKeeperEnterAddressManually"
  implicit val Key = CacheKey[NewKeeperEnterAddressManuallyFormModel](NewKeeperEnterAddressManuallyCacheKey)

  object Form {
    final val AddressAndPostcodeId = "addressAndPostcode"
    final val Mapping = mapping(
      AddressAndPostcodeId -> AddressAndPostcodeViewModel.Form.Mapping
    )(NewKeeperEnterAddressManuallyFormModel.apply)(NewKeeperEnterAddressManuallyFormModel.unapply)
  }
}