package filters

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.filters.{EnsureServiceOpenFilter, DateTimeZoneService}
import utils.helpers.Config

class ServiceOpenFilter @Inject()(implicit config: Config,
                                  timeZone: DateTimeZoneService) extends EnsureServiceOpenFilter {
  private val millisPerHour = 3600000
  protected lazy val opening = config.opening * millisPerHour
  protected lazy val closing = config.closing * millisPerHour
  protected lazy val dateTimeZone = timeZone
  protected lazy val html = views.html.changekeeper.closed()
}
