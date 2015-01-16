package controllers.changeKeeper

import composition.WithApplication
import controllers.CompleteAndConfirm
import helpers.UnitSpec
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmFormModel.Form.{ConsentId, DateOfSaleId, MileageId}
import org.joda.time.LocalDate
import play.api.data.Form
import uk.gov.dvla.vehicles.presentation.common.mappings.DayMonthYear.{DayId, MonthId, YearId}
import pages.changekeeper.CompleteAndConfirmPage.YearDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.MonthDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.DayDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.MileageValid
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue

class CompleteAndConfirmFormSpec extends UnitSpec {

  "form" should {
    "accept if form is completed with all fields entered correctly" in new WithApplication {
      val model = formWithValidDefaults().get
      model.mileage should equal(Some("1000".toInt))
      model.dateOfSale should equal(new LocalDate(
        YearDateOfSaleValid.toInt,
        MonthDateOfSaleValid.toInt,
        DayDateOfSaleValid.toInt))
      model.consent should equal ("consent")
    }

    "accept if form is completed with mandatory fields only" in new WithApplication {
      val model = formWithValidDefaults(
        mileage = "").get
      model.mileage should equal(None)
      model.dateOfSale should equal(new LocalDate(
        YearDateOfSaleValid.toInt,
        MonthDateOfSaleValid.toInt,
        DayDateOfSaleValid.toInt))
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(dayDateOfSale = "", monthDateOfSale = "", yearDateOfSale = "", consent = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid", "error.required")
    }
  }

  "mileage" should {
    "not accept less than 0" in new WithApplication {
      formWithValidDefaults(mileage = "-1").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.min")
    }

    "not accept less than 999999" in new WithApplication {
      formWithValidDefaults(mileage = "1000000").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.max")
    }

    "not accept letters" in new WithApplication {
      formWithValidDefaults(mileage = "abc").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.number")
    }

    "not accept special characters %%" in new WithApplication {
      formWithValidDefaults(mileage = "%%").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.number")
    }

    "not accept special characters (" in new WithApplication {
      formWithValidDefaults(mileage = "(").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.number")
    }

    "accept if mileage is entered correctly" in new WithApplication {
      val model = formWithValidDefaults(mileage = MileageValid).get

      model.mileage should equal(Some(MileageValid.toInt))
    }
  }

  "date of sale" should {
//   "not accept a date in the future" in new WithApplication {
//     formWithValidDefaults(yearDateOfSale = "2500").errors.flatMap(_.messages) should contain theSameElementsAs
//       List("error.date.inTheFuture")
//   }

    "not accept an invalid day of 0" in new WithApplication {
      formWithValidDefaults(dayDateOfSale = "0").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept an invalid day of 32" in new WithApplication {
      formWithValidDefaults(dayDateOfSale = "32").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept an invalid month of 0" in new WithApplication {
      formWithValidDefaults(monthDateOfSale = "0").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept an invalid month of 13" in new WithApplication {
      formWithValidDefaults(monthDateOfSale = "13").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept special characters in day field" in new WithApplication {
      formWithValidDefaults(dayDateOfSale = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept special characters in month field" in new WithApplication {
      formWithValidDefaults(monthDateOfSale = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept special characters in year field" in new WithApplication {
      formWithValidDefaults(yearDateOfSale = "$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept letters in day field" in new WithApplication {
      formWithValidDefaults(dayDateOfSale = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept letters in month field" in new WithApplication {
      formWithValidDefaults(monthDateOfSale = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "not accept letters in year field" in new WithApplication {
      formWithValidDefaults(yearDateOfSale = "a").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.date.invalid")
    }

    "accept if date of sale is entered correctly" in new WithApplication {
      val model = formWithValidDefaults(
        dayDateOfSale = DayDateOfSaleValid,
        monthDateOfSale = MonthDateOfSaleValid,
        yearDateOfSale = YearDateOfSaleValid).get

      model.dateOfSale should equal (new LocalDate(
        YearDateOfSaleValid.toInt,
        MonthDateOfSaleValid.toInt,
        DayDateOfSaleValid.toInt))
    }
  }


  private def formWithValidDefaults(mileage: String = MileageValid,
                                    dayDateOfSale: String = DayDateOfSaleValid,
                                    monthDateOfSale: String = MonthDateOfSaleValid,
                                    yearDateOfSale: String = YearDateOfSaleValid,
                                    consent: String = ConsentTrue): Form[CompleteAndConfirmFormModel] = {
    injector.getInstance(classOf[CompleteAndConfirm])
      .form.bind(
        Map(
          MileageId -> mileage,
          s"$DateOfSaleId.$DayId" -> dayDateOfSale,
          s"$DateOfSaleId.$MonthId" -> monthDateOfSale,
          s"$DateOfSaleId.$YearId" -> yearDateOfSale,
          ConsentId -> consent
        )
      )
  }
}