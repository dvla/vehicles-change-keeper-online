package constrints

import helpers.UnitSpec
import play.api.data.validation.{Invalid, Valid}
import constraints.FleetNumber

final class FleetNumberUnitSpec extends UnitSpec {

  "fleetNumber" should {
    val validFleetNumbers = Seq("123456", "12345-")

    validFleetNumbers.foreach { num =>
      s"indicate the fleet number is valid: $num" in {
        FleetNumber.fleetNumber(num) should equal(Valid)
      }
    }

    val invalidFleetNumbers = Seq("", "1234567", "-12345", "123-45", "A")
    invalidFleetNumbers.foreach { num =>
      s"indicate the fleet number is not valid: $num" in {
        val result = FleetNumber.fleetNumber(num)
        result shouldBe an [Invalid]
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal ("error.fleetNumber")
      }
    }
  }
}