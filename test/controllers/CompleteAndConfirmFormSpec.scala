package controllers

import composition.WithApplication
import helpers.UnitSpec
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmFormModel.Form.ConsentId
import models.CompleteAndConfirmFormModel.Form.RegRightId
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue
import play.api.data.Form

class CompleteAndConfirmFormSpec extends UnitSpec {

  "form" should {
    "accept if form is completed with all fields entered correctly" in new WithApplication {
      val model = formWithValidDefaults().get
      model.consent should equal ("consent")
      model.regRight should equal ("consent")
    }

    "reject if form has no fields completed" in new WithApplication {
      formWithValidDefaults(regRight = "", consent = "")
        .errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.required", "error.required")

    }
    "reject if right to registration is not completed" in new WithApplication {
      formWithValidDefaults(regRight = "", consent = ConsentTrue)
        .errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.required")
    }
    "reject if consent is not completed" in new WithApplication {
      formWithValidDefaults(regRight = ConsentTrue, consent = "")
        .errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.required")
    }
  }

  private def formWithValidDefaults(regRight: String = ConsentTrue, consent: String = ConsentTrue): Form[CompleteAndConfirmFormModel] =
    injector.getInstance(classOf[CompleteAndConfirm]).form.bind(Map(RegRightId -> regRight, ConsentId -> consent))
}
