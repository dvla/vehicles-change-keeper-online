package webserviceclients.fakes

import org.joda.time.{DateTime, Instant}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import uk.gov.dvla.vehicles.presentation.common.views.models.DayMonthYear

final class FakeDateServiceImpl extends DateService {
  import webserviceclients.fakes.FakeDateServiceImpl.{DateOfAcquisitionDayValid, DateOfAcquisitionMonthValid, DateOfAcquisitionYearValid}

  override def today = DayMonthYear(
    DateOfAcquisitionDayValid.toInt,
    DateOfAcquisitionMonthValid.toInt,
    DateOfAcquisitionYearValid.toInt
  )

  override def now = Instant.now()

  override def dateTimeISOChronology: String = new DateTime(
    DateOfAcquisitionYearValid.toInt,
    DateOfAcquisitionMonthValid.toInt,
    DateOfAcquisitionDayValid.toInt,
    0,
    0).toString
}

object FakeDateServiceImpl {
  final val DateOfAcquisitionDayValid = "25"
  final val DateOfAcquisitionMonthValid = "11"
  final val DateOfAcquisitionYearValid = "1970"
}
