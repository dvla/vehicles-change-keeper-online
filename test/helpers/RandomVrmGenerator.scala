package helpers

import scala.collection.immutable.NumericRange.Inclusive
import scala.collection.mutable
import scala.collection.parallel.mutable
import scala.util.Random

object RandomVrmGenerator {
  private final val letters: Inclusive[Char] = 'A' to 'Z'
  private final val numbers: Inclusive[Char] = '0' to '9'
  private final val usedVrms = new scala.collection.mutable.HashSet[String]()

  def vrm = {
    // Create random reg in this format: YL07YBX
    s"${randomString(letters, 2)}${randomString(numbers, 2)}${randomString(letters, 3)}"
  }

  def docRef = randomString(numbers,11)

  def randomString(alphabet: Inclusive[Char], n: Int): String =
    Stream.continually(Random.nextInt(alphabet.size)).
      map(letter => alphabet(letter)).
      take(n).
      mkString


  def uniqueVrm: String = vrm match {
    case x if vrm.contains(x) => uniqueVrm
    case x =>
      usedVrms.add(x)
      x
  }
}