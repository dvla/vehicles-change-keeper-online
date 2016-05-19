package pages.common

import uk.gov.dvla.vehicles.presentation.common.views.widgets.Prototype.FeedbackId

object Feedback {
  final val EmailFeedbackLink = s"""<a id="${FeedbackId}"""" +
    s""" href="${controllers.routes.FeedbackController.present()}" target="_blank">"""
}
