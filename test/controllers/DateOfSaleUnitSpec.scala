package controllers

import composition.WithApplication
import helpers.{UnitSpec, CookieFactoryForUnitSpecs}
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.DateOfSaleFormModel
import models.DateOfSaleFormModel.DateOfSaleCacheKey
import models.DateOfSaleFormModel.Form.{DateOfSaleId, MileageId}
import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.DateTimeFormat
import pages.changekeeper.CompleteAndConfirmPage
import pages.changekeeper.DateOfSalePage.{MileageValid, DayDateOfSaleValid, MonthDateOfSaleValid, YearDateOfSaleValid}
import pages.changekeeper.NewKeeperChooseYourAddressPage
import pages.changekeeper.NewKeeperEnterAddressManuallyPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, contentAsString, defaultAwaitTimeout, LOCATION}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClearTextClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.NoCookieFlags
import uk.gov.dvla.vehicles.presentation.common.mappings.Date.{DayId, MonthId, YearId}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper

class DateOfSaleUnitSpec extends UnitSpec {
  implicit private final val sessionFactory = new ClearTextClientSideSessionFactory()(new NoCookieFlags)

  private val saleYear = org.joda.time.LocalDate.now.minusYears(2).getYear.toString

  "submit" should {
    "replace numeric mileage error message for with standard error message" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(mileage = "$$")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())

      val result = dateOfSale.submitWithDateCheck(request)
      val replacementMileageErrorMessage = "You must enter a valid mileage between 0 and 999999"
      replacementMileageErrorMessage.r.findAllIn(contentAsString(result)).length should equal(2)
    }

    "return a bad request if date of sale is not entered" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest(dayDateOfSale = "", monthDateOfSale = "", yearDateOfSale = "")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "redirect to next page when mandatory fields are complete for new keeper " +
      "and neither the date of disposal or the change date are present" in new WithApplication {
      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))

        CookieHelper.fetchCookiesFromHeaders(r).getModel[DateOfSaleFormModel] shouldEqual
          Some(DateOfSaleFormModel(
            Some(MileageValid.toInt),
            new LocalDate(YearDateOfSaleValid.toInt, MonthDateOfSaleValid.toInt, DayDateOfSaleValid.toInt)
          ))

        CookieHelper.fetchCookiesFromHeaders(r).find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) orElse
          fail(s"Did not add cookie for th: $DateOfSaleCacheKey ")
      }
    }

    "Not go to the next page when the date of acquisition is before the date of disposal " +
      "and return a bad request" in new WithApplication {
      // The date of acquisition is 19-10-${saleYear}
      val disposalDate = DateTime.parse(s"20-10-${saleYear}", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        val resultCookies = CookieHelper.fetchCookiesFromHeaders(r)
        resultCookies.getModel[DateOfSaleFormModel] shouldEqual None
        resultCookies.find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) shouldEqual None
      }
    }

    "not go to the next page when the date of acquisition is before the keeper change date " +
      "and return a bad request" in new WithApplication {
      //    The date of acquisition is 19-10-${saleYear}
      val changeKeeperDate = DateTime.parse(s"20-10-${saleYear}", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperChangeDate = Some(changeKeeperDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        val resultCookies = CookieHelper.fetchCookiesFromHeaders(r)
        resultCookies.getModel[DateOfSaleFormModel] shouldEqual None
        resultCookies.find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) shouldEqual None
      }
    }

    "Go to the next page when the date of acquisition is the same as the date of disposal" in new WithApplication {
      //    The date of acquisition is 19-10-${saleYear}
      val disposalDate = DateTime.parse(s"19-10-${saleYear}", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperEndDate = Some(disposalDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))
        val resultCookies = CookieHelper.fetchCookiesFromHeaders(r)
        resultCookies.getModel[DateOfSaleFormModel] shouldEqual
          Some(DateOfSaleFormModel(
            Some(MileageValid.toInt),
            new LocalDate(YearDateOfSaleValid.toInt, MonthDateOfSaleValid.toInt, DayDateOfSaleValid.toInt)
          ))

        resultCookies.find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) orElse
          fail(s"Did not add cookie for th: $DateOfSaleCacheKey ")
      }
    }

    "Go to the next page when the date of acquisition is the same as the keeper change date" in new WithApplication {
      //    The date of acquisition is 19-10-${saleYear}
      val changeDate = DateTime.parse(s"19-10-${saleYear}", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(keeperChangeDate = Some(changeDate)))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitNoDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))

        val resultCookies = CookieHelper.fetchCookiesFromHeaders(r)
        resultCookies.getModel[DateOfSaleFormModel] shouldEqual
          Some(DateOfSaleFormModel(
            Some(MileageValid.toInt),
            new LocalDate(YearDateOfSaleValid.toInt, MonthDateOfSaleValid.toInt, DayDateOfSaleValid.toInt)
          ))

        resultCookies.find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) orElse
          fail(s"Did not add cookie for th: $DateOfSaleCacheKey ")
      }
    }

    "call the micro service when both the date of disposal and the change date are present " +
      "and redirect to the next page" in new WithApplication {
      val year = org.joda.time.LocalDate.now.minusYears(1).getYear.toString
      val disposalDate = DateTime.parse(s"09-03-${year}", DateTimeFormat.forPattern("dd-MM-yyyy"))
      val changeDate = DateTime.parse(s"09-03-${year}", DateTimeFormat.forPattern("dd-MM-yyyy"))

      val request = buildCorrectlyPopulatedRequest()
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel(
          keeperEndDate = Some(disposalDate),
          keeperChangeDate = Some(changeDate)
        ))
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitNoDateCheck(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(CompleteAndConfirmPage.address))

        CookieHelper.fetchCookiesFromHeaders(r).getModel[DateOfSaleFormModel] shouldEqual
          Some(DateOfSaleFormModel(
            Some(MileageValid.toInt),
            new LocalDate(YearDateOfSaleValid.toInt, MonthDateOfSaleValid.toInt, DayDateOfSaleValid.toInt)
          ))

        CookieHelper.fetchCookiesFromHeaders(r).find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) orElse
          fail(s"Did not add cookie for th: $DateOfSaleCacheKey ")
      }
    }
  }

  "back" should {
    "go back to the NewKeeperEnterAddressManually if manual input was chosen" in new WithApplication {
      val request = FakeRequest().withCookies(CookieFactoryForUnitSpecs.newKeeperEnterAddressManually())
      val result = dateOfSale.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(NewKeeperEnterAddressManuallyPage.address))
      }
    }

    "go back to the NewKeeperEnterAddress if no manual input was chosen" in new WithApplication {
      val request = FakeRequest()
      val result = dateOfSale.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(NewKeeperChooseYourAddressPage.address))
      }
    }
  }

  private def buildCorrectlyPopulatedRequest(mileage: String = MileageValid,
                                              dayDateOfSale: String = DayDateOfSaleValid,
                                              monthDateOfSale: String = MonthDateOfSaleValid,
                                              yearDateOfSale: String = YearDateOfSaleValid) = {
    FakeRequest().withFormUrlEncodedBody(
      MileageId -> mileage,
      s"$DateOfSaleId.$DayId" -> dayDateOfSale,
      s"$DateOfSaleId.$MonthId" -> monthDateOfSale,
      s"$DateOfSaleId.$YearId" -> yearDateOfSale
    ).withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
     .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
  }

  private def dateOfSale = injector.getInstance(classOf[DateOfSale])

  private lazy val present =
    dateOfSale.present(FakeRequest()
      .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
      .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
    )
}
