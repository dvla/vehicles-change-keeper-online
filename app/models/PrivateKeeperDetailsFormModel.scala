package models

import org.joda.time.LocalDate
import play.api.data.Forms.{mapping, optional}
import play.api.data.{Forms, FormError, Mapping}
import play.api.data.format.Formatter
import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.{nonEmpty, pattern}
import play.api.libs.json.Json
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CacheKey
import common.mappings.Date.optionalDateOfBirth
import common.mappings.Email.email
import common.mappings.DriverNumber.driverNumber
import common.mappings.Postcode.postcode
import common.views.helpers.FormExtensions.nonEmptyTextWithTransform
import uk.gov.dvla.vehicles.presentation.common.mappings.{TitleType, TitlePickerString}

case class PrivateKeeperDetailsFormModel(title: TitleType,
                                         firstName: String, 
                                         lastName: String,
                                         dateOfBirth: Option[LocalDate],
                                         email: Option[String],
                                         driverNumber: Option[String],
                                         postcode: String
                                         )

object PrivateKeeperDetailsFormModel {
  implicit val JsonFormatTitleType = Json.format[TitleType]
  implicit val JsonFormat = Json.format[PrivateKeeperDetailsFormModel]
  final val PrivateKeeperDetailsCacheKey = "privateKeeperDetails"
  implicit val Key = CacheKey[PrivateKeeperDetailsFormModel](PrivateKeeperDetailsCacheKey)
  private val NameRegEx = """^[a-zA-Z0-9\s\-\"\,\.\']{1,}$""".r

  object Form {
    final val TitleId = "privatekeeper_title"
    final val FirstNameId = "privatekeeper_firstname"
    final val LastNameId = "privatekeeper_lastname"
    final val DateOfBirthId = "privatekeeper_dateofbirth"
    final val EmailId = "privatekeeper_email"
    final val DriverNumberId = "privatekeeper_drivernumber"
    final val PostcodeId = "privatekeeper_postcode"
    final val ConsentId = "consent"

    final val DriverNumberMaxLength = 16
    final val FirstNameMinLength = 1
    final val FirstNameAndTitleMaxLength = 26
    final val LastNameMinLength = 1
    final val LastNameMaxLength = 25

    def firstNameMapping: Mapping[String] =
      Forms.of(FirstNameFormatter.firstNameFormatter(TitleId)).verifying(nonEmpty)

    def lastNameMapping: Mapping[String] =
      nonEmptyTextWithTransform(_.trim)(LastNameMinLength, LastNameMaxLength) verifying validLastName

    def validLastName: Constraint[String] = pattern(
      regex = NameRegEx,
      name = "constraint.validLastName",
      error = "error.validLastName")

    final val Mapping = mapping(
      TitleId -> TitlePickerString.mapping,
      FirstNameId -> firstNameMapping,
      LastNameId -> lastNameMapping,
      DateOfBirthId -> optionalDateOfBirth,
      EmailId -> optional(email),
      DriverNumberId -> optional(driverNumber),
      PostcodeId -> postcode
    )(PrivateKeeperDetailsFormModel.apply)(PrivateKeeperDetailsFormModel.unapply)
  }

  private object FirstNameFormatter {
    private val titleFormatter = TitlePickerString.formatter

    private type R = Either[Seq[FormError], String]

    def firstNameFormatter(titleKey: String) = new Formatter[String] {
      override def bind(key: String, data: Map[String, String]): R = {
        def t = titleFormatter.bind(titleKey, data) match {
          case Left(errors) => ""
          case Right(title) => NewKeeperDetailsViewModel.getTitle(title).trim
        }

        def errors(errors: String*): R = Left(errors.map(FormError(key, _)))

        data.get(key).fold[R](Left(Seq[FormError](FormError(key, "error.validFirstName")))) { firstName =>
          firstName.trim.length match {
            case l if l < Form.FirstNameMinLength => errors("error.validFirstName")
            case l if l > Form.FirstNameAndTitleMaxLength - t.length =>
              errors("error.titlePlusFirstName.tooLong")
            case _ =>
              if (NameRegEx.pattern.matcher(firstName).matches()) Right(firstName)
              else errors("error.validFirstName")
          }
        }
      }

      override def unbind(key: String, value: String) = Map(key -> value)
    }
  }
}