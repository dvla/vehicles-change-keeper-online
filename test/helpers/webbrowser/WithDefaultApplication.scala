package helpers.webbrowser

import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Around
import org.specs2.specification.Scope
import play.api.test.{Helpers, FakeApplication}
import TestConfiguration.configureTestUrl

abstract class WithDefaultApplication extends Around with Scope with GlobalCreator {

  private implicit def implicitApp: FakeApplication = FakeApplication(withGlobal = Some(global))

  override def around[T: AsResult](t: => T): Result = configureTestUrl() {
    Helpers.running(implicitApp)(AsResult.effectively(t))
  }
}
