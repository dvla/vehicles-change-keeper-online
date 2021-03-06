package controllers

import Common.PrototypeHtml
import helpers.TestWithApplication
import helpers.changekeeper.CookieFactoryForUnitSpecs
import helpers.UnitSpec
import models.K2KCacheKeyPrefix.CookiePrefix
import org.mockito.invocation.InvocationOnMock
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{times, verify, when}
import org.mockito.stubbing.Answer
import pages.changekeeper.BusinessKeeperDetailsPage.BusinessNameValid
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid}
import pages.changekeeper.VehicleLookupPage
import play.api.i18n.Lang
import play.api.mvc.Cookies
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, LOCATION, OK, SET_COOKIE, contentAsString, defaultAwaitTimeout}
import scala.concurrent.Future
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{TrackingId, ClientSideSessionFactory}
import common.model.NewKeeperChooseYourAddressFormModel.Form.AddressSelectId
import common.model.NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
import common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import common.model.NewKeeperEnterAddressManuallyFormModel.newKeeperEnterAddressManuallyCacheKey
import common.services.DateServiceImpl
import common.testhelpers.CookieHelper.fetchCookiesFromHeaders
import common.testhelpers.CookieHelper.verifyCookieHasBeenDiscarded
import common.testhelpers.CookieHelper.verifyCookieHasNotBeenDiscarded
import common.webserviceclients.addresslookup.AddressLookupWebService
import common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl
import common.webserviceclients.healthstats.HealthStats
import utils.helpers.Config
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.selectedAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddressNotFound

class NewKeeperChooseYourAddressUnitSpec extends UnitSpec {

  "present" should {
    // NOTE: without the timeout here a org.specs2.execute.ErrorException is thrown and for subsequent test
    "display the page if private new keeper details cached" in new TestWithApplication {
      whenReady(presentWithPrivateNewKeeper, timeout) { r =>
        r.header.status should equal(OK)
      }
    }

    "display the page if business new keeper details cached" in new TestWithApplication {
      whenReady(presentWithBusinessNewKeeper) { r =>
        r.header.status should equal(OK)
      }
    }

    "display selected field when private new keeper cookie exists" in new TestWithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(selectedAddress))
      val result = newKeeperChooseYourAddressWithAddressFound.present(request)
      val content = contentAsString(result)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include(s"""<option value="$selectedAddress" selected>""")
    }

    "display selected field when business new keeper cookie exists" in new TestWithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(selectedAddress))
      val result = newKeeperChooseYourAddressWithAddressFound.present(request)
      val content = contentAsString(result)
      content should include(BusinessNameValid)
      content should include(s"""<option value="$selectedAddress" selected>""")
    }

    "display expected drop-down values for private keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest("")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$selectedAddress" >""")
    }

    "display expected drop-down values for business keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest("")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$selectedAddress" >""")
    }

    "display unselected field when cookie does not exist for private new keeper" in new TestWithApplication {
      val content = contentAsString(presentWithPrivateNewKeeper)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should not include "selected"
    }

    "display unselected field when cookie does not exist for business new keeper" in new TestWithApplication {
      val content = contentAsString(presentWithBusinessNewKeeper)
      content should include(BusinessNameValid)
      content should not include "selected"
    }

    "redirect to vehicle lookup page when present is called with no keeper details cached" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithAddressFound.present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "display prototype message when config set to true for new private keeper" in new TestWithApplication {
      contentAsString(presentWithPrivateNewKeeper) should include(PrototypeHtml)
    }

    "display prototype message when config set to true for new business keeper" in new TestWithApplication {
      contentAsString(presentWithBusinessNewKeeper) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new TestWithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithFakeWebService(
        isPrototypeBannerVisible = false
      ).present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "fetch the addresses for the new keeper's postcode from the address lookup micro service" in new TestWithApplication {
      val request = FakeRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks()
      val result = controller.present(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callAddresses(
          anyString(),
          any[TrackingId]
        )(any[Lang])
      }
    }
  }

  "submit" should {
    "redirect to complete and confirm page after a valid submit for private keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "redirect to vehicle tax or sorn page after a valid submit for business keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "return a bad request if not address selected for private keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "return a bad request if not address selected for business keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "display expected drop-down values when no address selected for private keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest("")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$selectedAddress" >""")
    }

    "display expected drop-down values when no address selected for business keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest("")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$selectedAddress" >""")
    }

    "redirect to vehicle lookup page when valid submit with keeper details cached" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to vehicle lookup page when bad submit with no keeper details cached" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "write cookies and remove enter address manually cookie when UPRN found for private keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          newKeeperEnterAddressManuallyCacheKey,
          newKeeperChooseYourAddressCacheKey,
          newKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(newKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(newKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(newKeeperDetailsCacheKey, cookies)
      }
    }

    "write cookies and remove enter address manually cookie " +
      "when UPRN found for business keeper" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressFound.submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          newKeeperEnterAddressManuallyCacheKey,
          newKeeperChooseYourAddressCacheKey,
          newKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(newKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(newKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(newKeeperDetailsCacheKey, cookies)
      }
    }

    "does not write cookies when UPRN not found" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithAddressNotFound.submit(request)
      whenReady(result) { r =>
        val cookies = r.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
        cookies.map(_.name) should contain noneOf(
          newKeeperEnterAddressManuallyCacheKey,
          newKeeperChooseYourAddressCacheKey
        )
      }
    }

    "still call the micro service to fetch back addresses " +
      "even though an invalid submission is made" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "")
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        addressFound = true
      )

      val result = controller.submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(addressServiceMock, times(1)).callAddresses(anyString(),any[TrackingId])(any[Lang])
      }
    }

    "call the micro service to lookup the address by UPRN when a valid submission is made" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        addressFound = true
      )
      val result = controller.submit(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callAddresses(anyString(), any[TrackingId])(any[Lang])
      }
    }
  }

  private def newKeeperChooseYourAddressWithFakeWebService(addressFound: Boolean = true,
                                                          isPrototypeBannerVisible: Boolean = true) = {
    val responsePostcode = if (addressFound) responseValidForPostcodeToAddress
                           else responseValidForPostcodeToAddressNotFound
    val fakeWebService = new FakeAddressLookupWebServiceImpl(responsePostcode)
    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] =
        invocation.getArguments()(1).asInstanceOf[Future[_]]
    })

    val addressLookupService = new AddressLookupServiceImpl(fakeWebService, new DateServiceImpl, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(isPrototypeBannerVisible) // Stub this config value.
    when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
    when(config.assetsUrl).thenReturn(None) // Stub this config value.
    new NewKeeperChooseYourAddress(addressLookupService)
  }

  private def newKeeperChooseYourAddressControllerAndMocks(addressFound: Boolean = true,
                                                           isPrototypeBannerVisible: Boolean = true):
                                                          (NewKeeperChooseYourAddress, AddressLookupWebService) = {

    val responsePostcode = if (addressFound) responseValidForPostcodeToAddress
                           else responseValidForPostcodeToAddressNotFound

    val addressLookupWebServiceMock = mock[AddressLookupWebService]
    when(addressLookupWebServiceMock.callAddresses(
      anyString(),
      any[TrackingId]
    )(any[Lang]))
      .thenReturn(responsePostcode)

    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] =
        invocation.getArguments()(1).asInstanceOf[Future[_]]
    })

    val addressLookupService = new AddressLookupServiceImpl(
                                    addressLookupWebServiceMock,
                                    new DateServiceImpl,
                                    healthStatsMock)

    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(isPrototypeBannerVisible) // Stub this config value.
    when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
    when(config.assetsUrl).thenReturn(None) // Stub this config value.

    (new NewKeeperChooseYourAddress(addressLookupService), addressLookupWebServiceMock)
  }

  private def buildCorrectlyPopulatedRequest(newKeeperUprn: String = selectedAddress) = {
    FakeRequest().withFormUrlEncodedBody(
      AddressSelectId -> newKeeperUprn)
  }

  private def newKeeperChooseYourAddressWithAddressFound = newKeeperChooseYourAddressWithFakeWebService()

  private def newKeeperChooseYourAddressWithAddressNotFound =
    newKeeperChooseYourAddressWithFakeWebService(addressFound = false)

  private def presentWithPrivateNewKeeper = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
    newKeeperChooseYourAddressWithAddressFound.present(request)
  }

  private def presentWithBusinessNewKeeper = {
    val request = FakeRequest()
      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
    newKeeperChooseYourAddressWithAddressFound.present(request)
  }
}
