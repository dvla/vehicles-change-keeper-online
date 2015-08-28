package helpers.webbrowser

object ProgressBar {
  def progressStep(currentStep: Int): String = {
    val end = 10
    s"Step $currentStep of $end"
  }

  final val div: String = """<div class="progress-indicator">"""
}
