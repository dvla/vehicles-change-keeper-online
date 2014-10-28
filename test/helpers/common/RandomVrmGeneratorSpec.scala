package helpers.common

import helpers.UnitSpec

final class RandomVrmGeneratorSpec extends UnitSpec {

  "Random vrm generator" should {
    "not generate the same vrm twice" in {
      val first = RandomVrmGenerator.vrm
      val second = RandomVrmGenerator.vrm
      first should not equal second
    }

    "not use the same character in each section of the vrm eg AA11BBB" in {
      val vrm = RandomVrmGenerator.vrm

      // If the number of distinct characters is not > 3 this means each of the 3 sections
      // has the same character used multiple times and so the randomization is not enough
      vrm.distinct.size > 3 should equal(true)
    }
  }
}