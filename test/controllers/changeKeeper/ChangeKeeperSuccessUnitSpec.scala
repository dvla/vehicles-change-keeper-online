package controllers.changeKeeper

import controllers.changeKeeper.Common.PrototypeHtml
import controllers.ChangeKeeperSuccess
import helpers.{CookieFactoryForUnitSpecs, UnitSpec}
import uk.gov.dvla.vehicles.presentation.common
import models.K2KCacheKeyPrefix.CookiePrefix
import models.{PrivateKeeperDetailsFormModel, NewKeeperDetailsViewModel}
import models.{VehicleLookupFormModel, CompleteAndConfirmFormModel}
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import common.model.BusinessKeeperDetailsFormModel
import BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.joda.time.format.DateTimeFormat
import org.mockito.Mockito.when
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.CompleteAndConfirmPage.{DayDateOfSaleValid, MonthDateOfSaleValid, YearDateOfSaleValid}
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid, EmailValid}
import pages.changekeeper.BusinessKeeperDetailsPage.FleetNumberValid
import pages.changekeeper.BusinessKeeperDetailsPage.BusinessNameValid
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest, WithApplication}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper.{fetchCookiesFromHeaders, verifyCookieHasBeenDiscarded}
import utils.helpers.Config
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{TransactionTimestampValid, TransactionIdValid, RegistrationNumberValid}
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{VehicleMakeValid, VehicleModelValid}

class ChangeKeeperSuccessUnitSpec extends UnitSpec {

  "present" should {
    "display the page with new keeper cached" in new WithApplication {
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
      implicit val config: Config = mock[Config]
      when(config.isPrototypeBannerVisible).thenReturn(false)

      val acquireSuccessPrototypeNotVisible = new ChangeKeeperSuccess()
      val result = acquireSuccessPrototypeNotVisible.present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "redirect to before you start when no completion cookie is present" in new WithApplication {
      val request = FakeRequest()
      val result = changeKeeperSuccess.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }

    "present a full page with private keeper cached details when all cookies are present for new keeper success" in new WithApplication {
      val fmt = DateTimeFormat.forPattern("dd/MM/yyyy")

      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel(
          firstName = Some(FirstNameValid),
          lastName = Some(LastNameValid),
          email = Some(EmailValid)
        ))

      val content = contentAsString(changeKeeperSuccess.present(request))
      content should include(RegistrationNumberValid)
      content should include(VehicleMakeValid)
      content should include(VehicleModelValid)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include(EmailValid)
      content should include(YearDateOfSaleValid)
      content should include(MonthDateOfSaleValid)
      content should include(DayDateOfSaleValid)
      content should include(fmt.print(TransactionTimestampValid))
      content should include(TransactionIdValid)
    }

    "present a full page with business keeper cached details when all cookies are present for new keeper success" in new WithApplication {
      val fmt = DateTimeFormat.forPattern("dd/MM/yyyy")

      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel(
          businessName = Some(BusinessNameValid),
          fleetNumber = Some(FleetNumberValid),
          email = Some(EmailValid)
        ))

      val content = contentAsString(changeKeeperSuccess.present(request))
      content should include(RegistrationNumberValid)
      content should include(VehicleMakeValid)
      content should include(VehicleModelValid)
      content should include(BusinessNameValid)
      content should include(FleetNumberValid)
      content should include(EmailValid)
      content should include(YearDateOfSaleValid)
      content should include(MonthDateOfSaleValid)
      content should include(DayDateOfSaleValid)
      content should include(fmt.print(TransactionTimestampValid))
      content should include(TransactionIdValid)
    }
}

  "finish" should {
    "discard the vehicle, new keeper and confirm cookies" in {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)

        verifyCookieHasBeenDiscarded(VehicleAndKeeperLookupDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(VehicleLookupFormModelCacheKey, cookies)
        verifyCookieHasBeenDiscarded(NewKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(PrivateKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(businessKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(CompleteAndConfirmCacheKey, cookies)
        verifyCookieHasBeenDiscarded(ChangeKeeperCompletionResponseCacheKey, cookies)
      }
    }

    "redirect to the before you start page" in {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(BeforeYouStartPage.address))
      }
    }
  }

  private lazy val changeKeeperSuccess = {
    injector.getInstance(classOf[ChangeKeeperSuccess])
  }

  private lazy val present = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
      withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel())
    changeKeeperSuccess.present(request)
  }
}
