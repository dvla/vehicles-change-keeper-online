package filters

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.filters.{EnsureServiceOpenFilter, DateTimeZoneService}
import utils.helpers.Config

class ServiceOpenFilter @Inject()(implicit config: Config,
                                  timeZone: DateTimeZoneService) extends EnsureServiceOpenFilter {
  protected lazy val opening = config.openingTimeMinOfDay
  protected lazy val closing = config.closingTimeMinOfDay
  protected lazy val dateTimeZone = timeZone
  protected lazy val html = views.html.changekeeper.closed("", "")
  override protected val closedDays = config.closedDays

  override def html(opening: String, closing: String) =
    views.html.changekeeper.closed(opening, closing)
}
