package views.changekeeper

object CompleteAndConfirm {
  final val BackId = "back"
  final val SubmitId = "submit"

  def modify(htmlArgs: Map[Symbol, Any], modify: Boolean): Map[Symbol, Any] =
    if (modify)
      htmlArgs + (Symbol("tabindex") -> -1)
    else
      htmlArgs
}