package controllers

import composition.WithApplication
import helpers.UnitSpec
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmFormModel.Form.ConsentId
import play.api.data.Form
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue

class CompleteAndConfirmFormSpec extends UnitSpec {

  "form" should {
    "accept if form is completed with all fields entered correctly" in new WithApplication {
      val model = formWithValidDefaults().get
      model.consent should equal ("consent")
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(consent = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.required")
    }
  }

  private def formWithValidDefaults(consent: String = ConsentTrue): Form[CompleteAndConfirmFormModel] =
    injector.getInstance(classOf[CompleteAndConfirm]).form.bind(Map(ConsentId -> consent))
}
