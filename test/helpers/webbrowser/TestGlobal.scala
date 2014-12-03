package helpers.webbrowser

import composition.{GlobalLike, TestComposition}

object TestGlobal extends GlobalLike with TestComposition

trait TestGlobalCreator extends GlobalCreator {
  override def global = TestGlobal
}
