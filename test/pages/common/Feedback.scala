package pages.common

object Feedback {
  final val CommonSubject = "Private%20sale%20of%20a%20vehicle"
  final val EmailFeedbackLink = "<a id=\"feedback\" href=\"mailto:vm.feedback@digital.dvla.gov.uk?" +
    s"""Subject=$CommonSubject%20feedback\">"""

  final val EmailHelpLink = "<a href=\"mailto:julie.riseley@dvla.gsi.gov.uk?" +
    s"""Subject=$CommonSubject%20help\">"""
}
