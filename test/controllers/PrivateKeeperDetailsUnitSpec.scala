package controllers

import Common.PrototypeHtml
import composition.WithApplication
import helpers.CookieFactoryForUnitSpecs
import helpers.UnitSpec
import org.mockito.Mockito.when
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.PrivateKeeperDetailsPage.DayDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.DriverNumberValid
import pages.changekeeper.PrivateKeeperDetailsPage.EmailValid
import pages.changekeeper.PrivateKeeperDetailsPage.FirstNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.LastNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.MonthDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.PostcodeValid
import pages.changekeeper.PrivateKeeperDetailsPage.YearDateOfBirthValid
import pages.changekeeper.VehicleLookupPage
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, contentAsString, defaultAwaitTimeout, LOCATION, OK}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.mappings.OptionalToggle
import common.mappings.TitlePickerString
import common.mappings.TitlePickerString.standardOptions
import common.mappings.TitleType
import common.model.PrivateKeeperDetailsFormModel.Form.DriverNumberId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailOptionId
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameId
import common.model.PrivateKeeperDetailsFormModel.Form.PostcodeId
import common.model.PrivateKeeperDetailsFormModel.Form.TitleId
import common.services.DateService
import utils.helpers.Config

class PrivateKeeperDetailsUnitSpec extends UnitSpec {

  "present" should {
    "display the page" in new WithApplication {
      whenReady(present) { r =>
        r.header.status should equal(OK)
      }
    }

    "display prototype message when config set to true" in new WithApplication {
      contentAsString(present) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest()
      implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
      implicit val config = mock[Config]
      implicit val dateService = injector.getInstance(classOf[DateService])
      when(config.isPrototypeBannerVisible).thenReturn(false)

      val privateKeeperDetailsPrototypeNotVisible = new PrivateKeeperDetails()
      val result = privateKeeperDetailsPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "display populated fields when cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = privateKeeperDetails.present(request)
      val content = contentAsString(result)
      content should include(Messages(standardOptions.head))
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include(DayDateOfBirthValid)
      content should include(MonthDateOfBirthValid)
      content should include(YearDateOfBirthValid)
      content should include(EmailValid)
    }

    "display populated other title when cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel(title = TitleType(4, "otherTitle")))
      val result = privateKeeperDetails.present(request)
      val content = contentAsString(result)
      content should include("otherTitle")
    }

    "display empty fields when cookie does not exist" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = privateKeeperDetails.present(request)
      val content = contentAsString(result)
      content should include(Messages(standardOptions.head))
      content should not include "selected"
    }

    "redirect to vehicle lookup page when no cookie is present" in new WithApplication {
      val request = FakeRequest()
      val result = privateKeeperDetails.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }
  }

  "submit" should {
    "redirect to next page when mandatory fields are complete" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(email = "avalid@email.address")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal (Some(NewKeeperChooseYourAddressPage.address))
      }
    }

    "redirect to next page when all fields are complete" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal (Some(NewKeeperChooseYourAddressPage.address))
      }
    }

    "redirect to vehicle lookup page when no cookie is present" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(title = "2")
      val result = privateKeeperDetails.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "return a bad request if no details are entered" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(title = "",
                                                   firstName = "",
                                                   lastName = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "replace required error message for first name with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(firstName = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      val errorMessage = "First name - Must contain between 1 and 25 characters from the following A-Z, " +
                          "hyphen, apostrophe, full stop and space"
      val count = errorMessage.r.findAllIn(contentAsString(result)).length
      count should equal(1)
    }

    "replace required error message for last name with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(lastName = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      val errorMessage = "Last name - Must contain between 1 and 25 characters from the following A-Z, " +
                          "hyphen, apostrophe, full stop and space"
      val count = errorMessage.r.findAllIn(contentAsString(result)).length
      count should equal(1)
    }

    "replace required error message for postcode with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(postcode = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = privateKeeperDetails.submit(request)
      val errorMessage = "Must be between five and eight characters and in a valid format, e.g. AB1 2BA or AB12BA"
      val count = errorMessage.r.findAllIn(contentAsString(result)).length
      count should equal(2)
    }
  }

  private def buildCorrectlyPopulatedRequest(title: String = "1",
                                             firstName: String = FirstNameValid,
                                             lastName: String = LastNameValid,
                                             email: String = EmailValid,
                                             driverNumber: String = DriverNumberValid,
                                             postcode: String = PostcodeValid) = {
    FakeRequest().withFormUrlEncodedBody(
      s"$TitleId.${TitlePickerString.TitleRadioKey}" -> title,
      FirstNameId -> firstName,
      LastNameId -> lastName,
      EmailOptionId -> OptionalToggle.Visible,
      s"$EmailId.$EmailEnterId" -> email,
      s"$EmailId.$EmailVerifyId" -> email,
      DriverNumberId -> driverNumber,
      PostcodeId -> postcode
    )
  }

  private lazy val privateKeeperDetails = {
    injector.getInstance(classOf[PrivateKeeperDetails])
  }

  private lazy val present = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
    privateKeeperDetails.present(request)
  }
}
