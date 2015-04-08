package controllers.changeKeeper

import controllers.changeKeeper.Common.PrototypeHtml
import controllers.ChangeKeeperSuccess
import helpers.{CookieFactoryForUnitSpecs, UnitSpec}
import models.DateOfSaleFormModel
import models.K2KCacheKeyPrefix.CookiePrefix
import models.CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.joda.time.format.DateTimeFormat
import org.mockito.Mockito.when
import pages.changekeeper.BeforeYouStartPage
import pages.changekeeper.BusinessKeeperDetailsPage.FleetNumberValid
import pages.changekeeper.BusinessKeeperDetailsPage.BusinessNameValid
import pages.changekeeper.DateOfSalePage.{DayDateOfSaleValid, MonthDateOfSaleValid, YearDateOfSaleValid}
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid, EmailValid}
import play.api.test.Helpers.{LOCATION, OK, contentAsString, defaultAwaitTimeout}
import play.api.test.{FakeRequest}
import composition.WithApplication
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.ClientSideSessionFactory
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import common.testhelpers.CookieHelper.{fetchCookiesFromHeaders, verifyCookieHasBeenDiscarded}
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
        withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel()).
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
        withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel()).
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
        withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
        withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())

      val result = changeKeeperSuccess.finish(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)

        verifyCookieHasBeenDiscarded(vehicleAndKeeperLookupDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(VehicleLookupFormModelCacheKey, cookies)
        verifyCookieHasBeenDiscarded(newKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(privateKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(businessKeeperDetailsCacheKey, cookies)
        verifyCookieHasBeenDiscarded(DateOfSaleFormModel.DateOfSaleCacheKey, cookies)
        verifyCookieHasBeenDiscarded(CompleteAndConfirmCacheKey, cookies)
        verifyCookieHasBeenDiscarded(ChangeKeeperCompletionResponseCacheKey, cookies)
      }
    }

    "redirect to the before you start page" in {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel()).
        withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel()).
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
      withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel()).
      withCookies(CookieFactoryForUnitSpecs.completeAndConfirmModel()).
      withCookies(CookieFactoryForUnitSpecs.completeAndConfirmResponseModelModel())
    changeKeeperSuccess.present(request)
  }
}
