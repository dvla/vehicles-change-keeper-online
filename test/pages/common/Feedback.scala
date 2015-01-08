package pages.common

object Feedback {
  final val CommonSubject = "Private%20sale%20of%20a%20vehicle"
  final val EmailFeedbackLink = s"""<a id="${views.common.Feedback.FeedbackId}" href="" onclick="window.open('${controllers.routes.FeedbackController.present()}', '_blank')">"""

  final val EmailHelpLink = "<a href=\"mailto:julie.riseley@dvla.gsi.gov.uk?" +
    s"""Subject=$CommonSubject%20help\">"""
}
