package controllers

import Common.PrototypeHtml
import composition.WithApplication
import helpers.CookieFactoryForUnitSpecs
import helpers.UnitSpec
import models.K2KCacheKeyPrefix.CookiePrefix
import org.mockito.invocation.InvocationOnMock
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{never, times, verify, when}
import org.mockito.stubbing.Answer
import pages.changekeeper.BusinessKeeperDetailsPage.BusinessNameValid
import pages.changekeeper.DateOfSalePage
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid}
import pages.changekeeper.VehicleLookupPage
import pages.common.UprnNotFoundPage
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
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.UprnValid
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddressNotFound
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForUprnToAddressNotFound

class NewKeeperChooseYourAddressUnitSpec extends UnitSpec {
  "present (use UPRN enabled)" should {
    "display the page if private new keeper details cached" in new WithApplication {
      whenReady(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = true), timeout) { r =>
        r.header.status should equal(OK)
      }
    }

    "display the page if business new keeper details cached" in new WithApplication {
      whenReady(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = true), timeout) { r =>
        r.header.status should equal(OK)
      }
    }

    "display selected field when private new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(UprnValid.toString))
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).present(request)
      val content = contentAsString(result)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include(s"""<option value="$UprnValid" selected>""")
    }

    "display selected field when business new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(UprnValid.toString))
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).present(request)
      val content = contentAsString(result)
      content should include(BusinessNameValid)
      content should include(s"""<option value="$UprnValid" selected>""")
    }

    "display expected drop-down values for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$UprnValid" >""")
    }

    "display expected drop-down values for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$UprnValid" >""")
    }

    "display unselected field when cookie does not exist for private new keeper" in new WithApplication {
      val content = contentAsString(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = true))
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should not include "selected"
    }

    "display unselected field when cookie does not exist for business new keeper" in new WithApplication {
      val content = contentAsString(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = true))
      content should include(BusinessNameValid)
      content should not include "selected"
    }

    "redirect to vehicle lookup page when present is called with no keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "display prototype message when config set to true for new private keeper" in new WithApplication {
      contentAsString(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = true)) should include(PrototypeHtml)
    }

    "display prototype message when config set to true for new business keeper" in new WithApplication {
      contentAsString(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = true)) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithFakeWebService(
        isPrototypeBannerVisible = false,
        ordnanceSurveyUseUprn = true
      ).present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "fetch the addresses for the new keeper's postcode from the address lookup micro service" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(ordnanceSurveyUseUprn = true)
      val result = controller.present(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callPostcodeWebService(
          anyString(),
          any[TrackingId],
          any[Option[Boolean]]
        )(any[Lang])
      }
    }
  }

  "present (with UPRN disabled)" should {
    "display the page if private new keeper details cached" in new WithApplication {
      whenReady(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = false), timeout) { r =>
        r.header.status should equal(OK)
      }
    }

    "display the page if business new keeper details cached" in new WithApplication {
      whenReady(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = false), timeout) { r =>
        r.header.status should equal(OK)
      }
    }

    "display selected field when private new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).present(request)
      val content = contentAsString(result)
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should include("""<option value="0" selected>""")
    }

    "display selected field when business new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).present(request)
      val content = contentAsString(result)
      content should include(BusinessNameValid)
      content should include("""<option value="0" selected>""")
    }

    "display expected drop-down values for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include("""<option value="0" >""")
    }

    "display expected drop-down values for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include("""<option value="0" >""")
    }

    "display unselected field when cookie does not exist for private new keeper" in new WithApplication {
      val content = contentAsString(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = false))
      content should include(FirstNameValid)
      content should include(LastNameValid)
      content should not include "selected"
    }

    "display unselected field when cookie does not exist for business new keeper" in new WithApplication {
      val content = contentAsString(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = false))
      content should include(BusinessNameValid)
      content should not include "selected"
    }

    "redirect to vehicle lookup page when present is called with no keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).present(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "display prototype message when config set to true for new private keeper" in new WithApplication {
      contentAsString(presentWithPrivateNewKeeper(ordnanceSurveyUseUprn = false)) should include(PrototypeHtml)
    }

    "display prototype message when config set to true for new business keeper" in new WithApplication {
      contentAsString(presentWithBusinessNewKeeper(ordnanceSurveyUseUprn = false)) should include(PrototypeHtml)
    }

    "not display prototype message when config set to false" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithFakeWebService(
        isPrototypeBannerVisible = false,
        ordnanceSurveyUseUprn = false
      ).present(request)
      contentAsString(result) should not include PrototypeHtml
    }

    "fetch the addresses for the new keeper's postcode from the address lookup micro service" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(ordnanceSurveyUseUprn = false)
      val result = controller.present(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callPostcodeWebService(
          anyString(),
          any[TrackingId],
          any[Option[Boolean]]
        )(any[Lang])
      }
    }
  }

  "submit (with UPRN enabled)" should {
    "redirect to complete and confirm page after a valid submit for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "redirect to vehicle tax or sorn page after a valid submit for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "return a bad request if not address selected for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "return a bad request if not address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "display expected drop-down values when no address selected for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$UprnValid" >""")
    }

    "display expected drop-down values when no address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="$UprnValid" >""")
    }

    "redirect to vehicle lookup page when valid submit with keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to vehicle lookup page when bad submit with no keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but UPRN not found by the webservice " +
      "using new private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but UPRN not found by the webservice " +
      "using new business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "write cookies and remove enter address manually cookie when UPRN found for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
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
      "when UPRN found for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
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

    "does not write cookies when UPRN not found" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        val cookies = r.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
        cookies.map(_.name) should contain noneOf(
          newKeeperEnterAddressManuallyCacheKey,
          newKeeperChooseYourAddressCacheKey
        )
      }
    }

    "not call the micro service to lookup the address by UPRN " +
      "when an invalid submission is made" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        uprnFound = true,
        ordnanceSurveyUseUprn = true
      )
      val result = controller.submit(request)
      whenReady(result, timeout) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(addressServiceMock, never()).callUprnWebService(anyString(),any[TrackingId])(any[Lang])
      }
    }

    "call the micro service to lookup the address by UPRN when a valid submission is made" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        uprnFound = true,
        ordnanceSurveyUseUprn = true
      )
      val result = controller.submit(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callUprnWebService(anyString(), any[TrackingId])(any[Lang])
      }
    }
  }

  "submit (with UPRN disabled)" should {
    "redirect to vehicle tax or sorn page after a valid submit for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "redirect to vehicle tax or sorn page after a valid submit for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(DateOfSalePage.address))
      }
    }

    "return a bad request if no address selected for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "return a bad request if no address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "display expected drop-down values when no address selected for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="0" >""")
    }

    "display expected drop-down values when no address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include(s"""<option value="0" >""")
    }

    "redirect to vehicle lookup page when valid submit with keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to vehicle lookup page when bad submit with no keeper details cached" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(VehicleLookupPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but UPRN not found by the webservice " +
      "using new private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but UPRN not found by the webservice " +
      "using new business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "write cookies and remove enter address manually cookie when UPRN found for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
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
      "when UPRN found for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
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

    "does not write cookies when UPRN not found" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        val cookies = r.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
        cookies.map(_.name) should contain noneOf(
          newKeeperEnterAddressManuallyCacheKey,
          newKeeperChooseYourAddressCacheKey
          )
      }
    }

    "still call the micro service to fetch back addresses " +
      "even though an invalid submission is made" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())

      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        uprnFound = true,
        ordnanceSurveyUseUprn = false
      )
      val result = controller.submit(request)
      whenReady(result, timeout) { r =>
        r.header.status should equal(BAD_REQUEST)
        verify(addressServiceMock, times(1)).callPostcodeWebService(
          anyString(),
          any[TrackingId],
          any[Option[Boolean]]
        )(any[Lang])
      }
    }

    "call the micro service to lookup the address by postcode when a valid submission is made" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())

      val (controller, addressServiceMock) = newKeeperChooseYourAddressControllerAndMocks(
        uprnFound = true,
        ordnanceSurveyUseUprn = false
      )
      val result = controller.submit(request)
      whenReady(result) { r =>
        verify(addressServiceMock, times(1)).callPostcodeWebService(
          anyString(),
          any[TrackingId],
          any[Option[Boolean]]
        )(any[Lang])
      }
    }
  }

  private def newKeeperChooseYourAddressWithFakeWebService(uprnFound: Boolean = true,
                                                          isPrototypeBannerVisible: Boolean = true,
                                                          ordnanceSurveyUseUprn: Boolean) = {
    val responsePostcode = if (uprnFound) responseValidForPostcodeToAddress
                           else responseValidForPostcodeToAddressNotFound
    val responseUprn = if (uprnFound) responseValidForUprnToAddress else responseValidForUprnToAddressNotFound
    val fakeWebService = new FakeAddressLookupWebServiceImpl(responsePostcode, responseUprn)
    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] =
        invocation.getArguments()(1).asInstanceOf[Future[_]]
    })

    val addressLookupService = new AddressLookupServiceImpl(fakeWebService, new DateServiceImpl, healthStatsMock)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(isPrototypeBannerVisible) // Stub this config value.
    when(config.ordnanceSurveyUseUprn).thenReturn(ordnanceSurveyUseUprn) // Stub this config value.
    when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
    when(config.assetsUrl).thenReturn(None) // Stub this config value.
    new NewKeeperChooseYourAddress(addressLookupService)
  }

  private def newKeeperChooseYourAddressControllerAndMocks(uprnFound: Boolean = true,
                                                           isPrototypeBannerVisible: Boolean = true,
                                                           ordnanceSurveyUseUprn: Boolean): (NewKeeperChooseYourAddress,
                                                                                            AddressLookupWebService) = {
    val responsePostcode = if (uprnFound) responseValidForPostcodeToAddress
                           else responseValidForPostcodeToAddressNotFound
    val responseUprn = if (uprnFound) responseValidForUprnToAddress else responseValidForUprnToAddressNotFound

    val addressLookupWebServiceMock = mock[AddressLookupWebService]
    when(addressLookupWebServiceMock.callPostcodeWebService(
      anyString(),
      any[TrackingId],
      any[Option[Boolean]]
    )(any[Lang])).
      thenReturn(responsePostcode)
    when(addressLookupWebServiceMock.callUprnWebService(anyString(), any[TrackingId])(any[Lang])).
      thenReturn(responseUprn)

    val healthStatsMock = mock[HealthStats]
    when(healthStatsMock.report(anyString)(any[Future[_]])).thenAnswer(new Answer[Future[_]] {
      override def answer(invocation: InvocationOnMock): Future[_] =
        invocation.getArguments()(1).asInstanceOf[Future[_]]
    })

    val addressLookupService = new AddressLookupServiceImpl(
                                    addressLookupWebServiceMock,
                                    new DateServiceImpl,
                                    healthStatsMock
                                )

    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(isPrototypeBannerVisible) // Stub this config value.
    when(config.ordnanceSurveyUseUprn).thenReturn(ordnanceSurveyUseUprn) // Stub this config value.
    when(config.googleAnalyticsTrackingId).thenReturn(None) // Stub this config value.
    when(config.assetsUrl).thenReturn(None) // Stub this config value.

    (new NewKeeperChooseYourAddress(addressLookupService), addressLookupWebServiceMock)
  }

  private def buildCorrectlyPopulatedRequest(newKeeperUprn: String = UprnValid.toString) = {
    FakeRequest().withFormUrlEncodedBody(
      AddressSelectId -> newKeeperUprn)
  }

  private def newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn: Boolean) =
    newKeeperChooseYourAddressWithFakeWebService(ordnanceSurveyUseUprn = ordnanceSurveyUseUprn)

  private def newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn: Boolean) =
    newKeeperChooseYourAddressWithFakeWebService(uprnFound = false, ordnanceSurveyUseUprn = ordnanceSurveyUseUprn)

  private def presentWithPrivateNewKeeper(ordnanceSurveyUseUprn: Boolean) = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
    newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn).present(request)
  }

  private def presentWithBusinessNewKeeper(ordnanceSurveyUseUprn: Boolean) = {
    val request = FakeRequest().
      withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
      withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
    newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn).present(request)
  }
}
