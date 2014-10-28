package composition

import com.google.inject.Guice
import filters.ServiceOpenFilter
import play.filters.gzip.GzipFilter
import uk.gov.dvla.vehicles.presentation.common
import common.filters.{EnsureSessionCreatedFilter, AccessLoggingFilter, CsrfPreventionFilter}
import utils.helpers.ErrorStrategy

trait Composition {
  lazy val injector = Guice.createInjector(DevModule)

  lazy val filters = Array(
    injector.getInstance(classOf[EnsureSessionCreatedFilter]),
    new GzipFilter(),
    injector.getInstance(classOf[AccessLoggingFilter]),
    injector.getInstance(classOf[CsrfPreventionFilter]),
    injector.getInstance(classOf[ServiceOpenFilter])
  )

  lazy val errorStrategy = injector.getInstance(classOf[ErrorStrategy])
}