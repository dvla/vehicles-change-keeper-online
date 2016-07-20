package controllers

import helpers.{TestWithApplication, UnitSpec}
import helpers.changekeeper.CookieFactoryForUnitSpecs
import models.CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey
import models.DateOfSaleFormModel
import models.DateOfSaleFormModel.DateOfSaleCacheKey
import models.DateOfSaleFormModel.Form.{DateOfSaleId, MileageId}
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, LocalDate}
import pages.changekeeper.DateOfSalePage.{DayDateOfSaleValid, MileageValid, MonthDateOfSaleValid, YearDateOfSaleValid, ValidDateOfSale}
import pages.changekeeper.{CompleteAndConfirmPage, NewKeeperChooseYourAddressPage, NewKeeperEnterAddressManuallyPage}
import play.api.test.FakeRequest
import play.api.test.Helpers.{BAD_REQUEST, LOCATION, contentAsString, defaultAwaitTimeout}
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.CookieImplicits.RichCookies
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.{ClearTextClientSideSessionFactory, NoCookieFlags}
import uk.gov.dvla.vehicles.presentation.common.mappings.Date.{DayId, MonthId, YearId}
import uk.gov.dvla.vehicles.presentation.common.testhelpers.CookieHelper

class DateOfSaleUnitSpec extends UnitSpec {
  implicit private final val sessionFactory = new ClearTextClientSideSessionFactory()(new NoCookieFlags)

  "submit" should {
    "replace numeric mileage error message for with standard error message" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(mileage = "$$")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.privateKeeperDetailsModel())

      val result = dateOfSale.submitWithDateCheck(request)
      val replacementMileageErrorMessage = "You must enter a valid mileage between 0 and 999999"
      replacementMileageErrorMessage.r.findAllIn(contentAsString(result)).length should equal(2)
    }

    "return a bad request if date of sale is not entered" in new TestWithApplication {
      val request = buildCorrectlyPopulatedRequest(dayDateOfSale = "", monthDateOfSale = "", yearDateOfSale = "")
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "redirect to next page when mandatory fields are complete for new keeper " +
      "and neither the date of disposal or the change date are present" in new TestWithApplication {
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

    "not go to the next page when the date of sale is before the date of disposal " +
      "and return a bad request" in new TestWithApplication {
      val disposalDate = ValidDateOfSale.plusDays(1)

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

    "not go to the next page when the date of sale is before the keeper change date " +
      "and return a bad request" in new TestWithApplication {
      val changeKeeperDate = ValidDateOfSale.plusDays(1)

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

    "not go to the next page when the date of sale is over 12 months" in new TestWithApplication {
      val invalidDateOfSale = ValidDateOfSale.minusYears(1)

      val request = buildCorrectlyPopulatedRequest(
        dayDateOfSale = invalidDateOfSale.toString("dd"),
        monthDateOfSale = invalidDateOfSale.toString("MM"),
        yearDateOfSale = invalidDateOfSale.getYear.toString
      )
        .withCookies(CookieFactoryForUnitSpecs.newKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleLookupFormModel())
        .withCookies(CookieFactoryForUnitSpecs.vehicleAndKeeperDetailsModel())
        .withCookies(CookieFactoryForUnitSpecs.sellerEmailModel())

      val result = dateOfSale.submitWithDateCheck(request)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
        val resultCookies = CookieHelper.fetchCookiesFromHeaders(r)
        resultCookies.getModel[DateOfSaleFormModel] shouldEqual None
        resultCookies.find(_.name == AllowGoingToCompleteAndConfirmPageCacheKey) shouldEqual None
      }
    }

    "go to the next page when the date of sale is the same as the date of disposal" in new TestWithApplication {
      val disposalDate = ValidDateOfSale

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

    "go to the next page when the date of sale is the same as the keeper change date" in new TestWithApplication {
      //    The date of sale is 19-10-${saleYear}
      val changeDate = ValidDateOfSale

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
      "and redirect to the next page" in new TestWithApplication {
      val disposalDate = ValidDateOfSale
      val changeDate = ValidDateOfSale

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
    "go back to the NewKeeperEnterAddressManually if manual input was chosen" in new TestWithApplication {
      val request = FakeRequest().withCookies(CookieFactoryForUnitSpecs.newKeeperEnterAddressManually())
      val result = dateOfSale.back(request)
      whenReady(result) { r =>
        r.header.headers.get(LOCATION) should equal(Some(NewKeeperEnterAddressManuallyPage.address))
      }
    }

    "go back to the NewKeeperEnterAddress if no manual input was chosen" in new TestWithApplication {
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
