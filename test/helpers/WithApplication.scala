package helpers

import helpers.WithApplication.fakeAppWithTestGlobal
import helpers.webbrowser.TestConfiguration
import org.specs2.execute.{AsResult, Result}
import play.api.test.FakeApplication

abstract class WithApplication(app: FakeApplication = fakeAppWithTestGlobal)
  extends play.api.test.WithApplication(app = app) {
  override def around[T: AsResult](t: => T): Result = {
    TestConfiguration.configureTestUrl()(super.around(t))
  }
}

object WithApplication {
  private lazy val fakeAppWithTestGlobal: FakeApplication = FakeApplication(withGlobal = Some(TestGlobal))
}
