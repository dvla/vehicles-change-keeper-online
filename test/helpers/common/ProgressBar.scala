package helpers.common

import helpers.webbrowser.GlobalCreator
import play.api.test.FakeApplication

trait ProgressBar {
  self: GlobalCreator =>

  val fakeApplicationWithProgressBarFalse = FakeApplication(
    withGlobal = Some(global),
    additionalConfiguration = Map("progressBar.enabled" -> "false"))

  val fakeApplicationWithProgressBarTrue = FakeApplication(
    withGlobal = Some(global),
    additionalConfiguration = Map("progressBar.enabled" -> "true"))
}

object ProgressBar {
  def progressStep(currentStep: Int): String = {
    val end = 9
    s"Step $currentStep of $end"
  }

  final val div: String = """<div class="progress-indicator">"""
}
