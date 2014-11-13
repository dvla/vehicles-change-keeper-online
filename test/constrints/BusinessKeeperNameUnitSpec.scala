package constrints

import helpers.UnitSpec
import play.api.data.validation.{Invalid, Valid}
import constraints.BusinessKeeperName

final class BusinessKeeperNameUnitSpec extends UnitSpec {

  "BusinessKeeperName" should {
    val validBusinessKeeperName = Seq(
      "qwertyuiopasdfghjklzxcvbnmqwer",
      "qw",
      "12",
      "q-",
      "q,",
      "q'"
    )
    validBusinessKeeperName.foreach { businessKeeperName =>
      s"indicate the input is valid for: $businessKeeperName" in {
        BusinessKeeperName.validBusinessKeeperName(businessKeeperName) should equal(Valid)
      }
    }

    val invalidBusinessKeeperName = Seq(
      "£wertyuiopqwertyuiopqwertyuiop",
      "%wertyuiopqwertyuiopqwertyuiop",
      "w*",
      "q+",
      "q!",
      "q\"",
      "q£",
      "q$",
      "q%",
      "q^",
      "q&",
      "q(",
      "q)"
    )
    invalidBusinessKeeperName.foreach { num =>
      s"indicate the input is not valid: $num" in {
        val result = BusinessKeeperName.validBusinessKeeperName(num)
        result shouldBe an [Invalid]
        val invalid = result.asInstanceOf[Invalid]
        invalid.errors(0).message should equal ("error.validBusinessKeeperName")
      }
    }
  }
}