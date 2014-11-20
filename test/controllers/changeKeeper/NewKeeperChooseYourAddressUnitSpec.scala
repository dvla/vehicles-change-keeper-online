package controllers.changeKeeper

import controllers.NewKeeperChooseYourAddress
import helpers.changekeeper.CookieFactoryForUnitSpecs
import helpers.{UnitSpec, WithApplication}
import helpers.common.CookieHelper.{fetchCookiesFromHeaders, verifyCookieHasBeenDiscarded, verifyCookieHasNotBeenDiscarded}
import models.NewKeeperChooseYourAddressFormModel.Form.AddressSelectId
import models.NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import org.mockito.Mockito.when
import pages.changekeeper.{CompleteAndConfirmPage, VehicleLookupPage}
import pages.common.UprnNotFoundPage
import play.api.mvc.Cookies
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, LOCATION, OK, SET_COOKIE, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.model.TraderDetailsModel.TraderDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl
import utils.helpers.Config
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid}
import pages.changekeeper.BusinessKeeperDetailsPage.BusinessNameValid
import Common.PrototypeHtml
import models.NewKeeperEnterAddressManuallyFormModel.NewKeeperEnterAddressManuallyCacheKey
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.UprnValid
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddressNotFound
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.responseValidForUprnToAddressNotFound

final class NewKeeperChooseYourAddressUnitSpec extends UnitSpec {
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
      content should include( s"""<option value="$UprnValid" selected>""")
    }

    "display selected field when business new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress(UprnValid.toString))
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).present(request)
      val content = contentAsString(result)
      content should include(BusinessNameValid)
      content should include( s"""<option value="$UprnValid" selected>""")
    }

    "display expected drop-down values for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="$UprnValid" >""")
    }

    "display expected drop-down values for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="$UprnValid" >""")
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
      val result = newKeeperChooseYourAddressWithFakeWebService(isPrototypeBannerVisible = false, ordnanceSurveyUseUprn = true).present(request)
      contentAsString(result) should not include PrototypeHtml
    }
  }
/*
  "present (use UPRN disabled)" should {
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
      content should include( s"""<option value="0" selected>""")
    }

    "display selected field when business new keeper cookie exists" in new WithApplication {
      val request = FakeRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.newKeeperChooseYourAddress())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).present(request)
      val content = contentAsString(result)
      content should include(BusinessNameValid)
      content should include( s"""<option value="0" selected>""")
    }

    "display expected drop-down values for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="0" >""")
    }

    "display expected drop-down values for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="0" >""")
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
      val result = newKeeperChooseYourAddressWithFakeWebService(isPrototypeBannerVisible = false, ordnanceSurveyUseUprn = false).present(request)
      contentAsString(result) should not include PrototypeHtml
    }
  }
*/
/*
  "submit (use UPRN enabled)" should {
    "redirect to complete and confirm page after a valid submit for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))
      }
    }

    "redirect to vehicle tax or sorn page after a valid submit for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))
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
      content should include( s"""<option value="$UprnValid" >""")
    }

    "display expected drop-down values when no address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="$UprnValid" >""")
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

    "redirect to UprnNotFound page when submit with but uprn not found by the webservice using new private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but uprn not found by the webservice using new business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "write cookies and remove enter address manually cookie when uprn found for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          NewKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(NewKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperDetailsCacheKey, cookies)
      }
    }

    "write cookies and remove enter address manually cookie when uprn found for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          NewKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(NewKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperDetailsCacheKey, cookies)
      }
    }

    "does not write cookies when uprn not found" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = true).submit(request)
      whenReady(result) { r =>
        val cookies = r.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
        cookies.map(_.name) should contain noneOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          TraderDetailsCacheKey
        )
      }
    }
  }

  "submit (use UPRN disabled)" should {
    "redirect to vehicle tax or sorn page after a valid submit for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))
      }
    }

    "redirect to vehicle tax or sorn page after a valid submit for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(newKeeperUprn = "0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))
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
      content should include( s"""<option value="0" >""")
    }

    "display expected drop-down values when no address selected for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      val content = contentAsString(result)
      content should include( s"""<option value="0" >""")
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

    "redirect to UprnNotFound page when submit with but uprn not found by the webservice using new private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "redirect to UprnNotFound page when submit with but uprn not found by the webservice using new business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(UprnNotFoundPage.address))
      }
    }

    "write cookies and remove enter address manually cookie when uprn found for private keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          NewKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(NewKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperDetailsCacheKey, cookies)
      }
    }

    "write cookies and remove enter address manually cookie when uprn found for business keeper" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest("0").
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel()).
        withCookies(CookieFactoryForUnitSpecs.businessKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        val cookies = fetchCookiesFromHeaders(r)
        cookies.map(_.name) should contain allOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          NewKeeperDetailsCacheKey
          )
        verifyCookieHasBeenDiscarded(NewKeeperEnterAddressManuallyCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperChooseYourAddressCacheKey, cookies)
        verifyCookieHasNotBeenDiscarded(NewKeeperDetailsCacheKey, cookies)
      }
    }

    "does not write cookies when uprn not found" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest().
        withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
      val result = newKeeperChooseYourAddressWithUprnNotFound(ordnanceSurveyUseUprn = false).submit(request)
      whenReady(result) { r =>
        val cookies = r.header.headers.get(SET_COOKIE).toSeq.flatMap(Cookies.decode)
        cookies.map(_.name) should contain noneOf(
          NewKeeperEnterAddressManuallyCacheKey,
          NewKeeperChooseYourAddressCacheKey,
          TraderDetailsCacheKey
          )
      }
    }
  }
*/
  private def newKeeperChooseYourAddressWithFakeWebService(uprnFound: Boolean = true,
                                                          isPrototypeBannerVisible: Boolean = true,
                                                          ordnanceSurveyUseUprn: Boolean) = {
    val responsePostcode = if (uprnFound) responseValidForPostcodeToAddress else responseValidForPostcodeToAddressNotFound
    val responseUprn = if (uprnFound) responseValidForUprnToAddress else responseValidForUprnToAddressNotFound
    val fakeWebService = new FakeAddressLookupWebServiceImpl(responsePostcode, responseUprn)
    val addressLookupService = new AddressLookupServiceImpl(fakeWebService)
    implicit val clientSideSessionFactory = injector.getInstance(classOf[ClientSideSessionFactory])
    implicit val config: Config = mock[Config]
    when(config.isPrototypeBannerVisible).thenReturn(isPrototypeBannerVisible) // Stub this config value.
    when(config.ordnanceSurveyUseUprn).thenReturn(ordnanceSurveyUseUprn) // Stub this config value.
    new NewKeeperChooseYourAddress(addressLookupService)
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