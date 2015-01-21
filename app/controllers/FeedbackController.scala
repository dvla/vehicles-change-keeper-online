package controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc._
import uk.gov.dvla.vehicles.presentation.common.clientsidesession.ClientSideSessionFactory
import uk.gov.dvla.vehicles.presentation.common.controllers.FeedbackBase
import uk.gov.dvla.vehicles.presentation.common.model.FeedbackForm
import utils.helpers.Config

class FeedbackController @Inject()()(implicit clientSideSessionFactory: ClientSideSessionFactory,
                                        config: Config) extends Controller with FeedbackBase {

  override val emailConfiguration = config.emailConfiguration

  private[controllers] val form = Form(
    FeedbackForm.Form.Mapping
  )

  private final val CookieErrorMessage = "Did not find VehicleDetailsModel cookie. Now redirecting to SetUpTradeDetails."

  implicit val controls: Map[String, Call] = Map(
  "submit" -> controllers.routes.FeedbackController.submit()
  )

  def present() = Action { implicit request =>
    Ok(views.html.changekeeper.feedback( form.fill(FeedbackForm(""))) )
  }

  def submit: Action[AnyContent] = Action { implicit request =>
    form.bindFromRequest.fold (
      invalidForm => BadRequest(views.html.changekeeper.feedback(invalidForm)),
        validForm => {
          sendFeedback(validForm.feedback, Messages("common_feedback.subject"))
          Ok(views.html.changekeeper.feedbackSuccess())
        }
    )
  }

}