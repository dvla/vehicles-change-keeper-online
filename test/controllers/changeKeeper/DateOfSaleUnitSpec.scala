package controllers.changeKeeper

import helpers.CookieFactoryForUnitSpecs
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.mockito.Matchers._
import org.mockito.Mockito._
import pages.changekeeper.ChangeKeeperSuccessPage
import play.api.test.Helpers._
import play.api.test.WithApplication
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.{AcquireService, AcquireRequestDto}
import webserviceclients.emailservice.{EmailServiceSendResponse, EmailService, EmailServiceSendRequest}
import webserviceclients.fakes.FakeAcquireWebServiceImpl._

import scala.concurrent.Future

class DateOfSaleUnitSpec {
//  "replace numeric mileage error message for with standard error message" in new WithApplication {
//    val request = buildCorrectlyPopulatedRequest(mileage = "$$")
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())
//
//    val result = completeAndConfirm.submit(request)
//    val replacementMileageErrorMessage = "You must enter a valid mileage between 0 and 999999"
//    replacementMileageErrorMessage.r.findAllIn(contentAsString(result)).length should equal(2)
//  }


//  "return a bad request if date of sale is not entered" in new WithApplication {
//    val request = buildCorrectlyPopulatedRequest(dayDateOfSale = "", monthDateOfSale = "", yearDateOfSale = "")
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.status should equal(BAD_REQUEST)
//    }
//  }
//  "redirect to next page when mandatory fields are complete for new keeper and neither the date of disposal or the change date are present" in new WithApplication {
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.dateOfSaleModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createMocks
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
//      verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, times(1)).invoke(any[EmailServiceSendRequest], anyString())
//    }
//  }
//
//  "not call the micro service when the date of acquisition is before the date of disposal and return a bad request" in new WithApplication {
//    The date of acquisition is 19-10-2012
//    val disposalDate = DateTime.parse("20-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))
//
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createMocks
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.status should equal(BAD_REQUEST)
//      verify(acquireServiceMock, never()).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, never()).invoke(any[EmailServiceSendRequest], anyString())
//    }
//  }
//
//  "not call the micro service when the date of acquisition is before the keeper change date and return a bad request" in new WithApplication {
//    The date of acquisition is 19-10-2012
//    val changeKeeperDate = DateTime.parse("20-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))
//
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperChangeDate = Some(changeKeeperDate)))
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createMocks
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.status should equal(BAD_REQUEST)
//      verify(acquireServiceMock, never()).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, never()).invoke(any[EmailServiceSendRequest], anyString())
//    }
//
//  }
//
//  "call the micro service when the date of acquisition is the same as the date of disposal and redirect to the next page" in new WithApplication {
//    The date of acquisition is 19-10-2012
//    val disposalDate = DateTime.parse("19-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))
//
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createMocks
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
//      verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, times(1)).invoke(any[EmailServiceSendRequest], anyString())
//    }
//  }
//
//  "call the micro service when the date of acquisition is the same as the keeper change date and redirect to the next page" in new WithApplication {
//    The date of acquisition is 19-10-2012
//    val changeDate = DateTime.parse("19-10-2012", DateTimeFormat.forPattern("dd-MM-yyyy"))
//
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperChangeDate = Some(changeDate)))
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val acquireServiceMock = mock[AcquireService]
//    val emailServiceMock = mock[EmailService]
//    when(emailServiceMock.invoke(any[EmailServiceSendRequest](), anyString())).
//      thenReturn(Future(EmailServiceSendResponse()))
//    val completeAndConfirm = completeAndConfirmController(acquireServiceMock, emailServiceMock)
//
//    when(acquireServiceMock.invoke(any[AcquireRequestDto], any[String])).
//      thenReturn(Future.successful {
//      (OK, Some(acquireResponseSuccess))
//    })
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
//      verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, times(1)).invoke(any[EmailServiceSendRequest], anyString())
//    }
//  }
//
//  "call the micro service when both the date of disposal and the change date are present and redirect to the next page" in new WithApplication {
//    The date of acquisition is 19-10-2012
//    val disposalDate = DateTime.parse("09-03-2015", DateTimeFormat.forPattern("dd-MM-yyyy"))
//    val changeDate = DateTime.parse("09-03-2015", DateTimeFormat.forPattern("dd-MM-yyyy"))
//
//    val request = buildCorrectlyPopulatedRequest()
//      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
//      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate), keeperChangeDate = Some(changeDate)))
//      .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())
//      .withCookies(CookieFactoryForUnitSpecs.allowGoingToCompleteAndConfirm())
//
//    val (acquireServiceMock, emailServiceMock, completeAndConfirm) = createMocks
//
//    val result = completeAndConfirm.submit(request)
//    whenReady(result) { r =>
//      r.header.headers.get(LOCATION) should equal(Some(ChangeKeeperSuccessPage.address))
//      verify(acquireServiceMock, times(1)).invoke(any[AcquireRequestDto], anyString())
//      verify(emailServiceMock, times(1)).invoke(any[EmailServiceSendRequest], anyString())
//    }
//  }
}
