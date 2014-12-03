package helpers

import composition.{GlobalLike, TestComposition}
import helpers.webbrowser.GlobalCreator

object TestGlobal extends GlobalLike with TestComposition

trait TestGlobalCreator extends GlobalCreator {
  override def global = TestGlobal
}
