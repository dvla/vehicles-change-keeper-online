package email

import helpers.UnitSpec

/**
 * Created by gerasimosarvanitis on 04/12/2014.
 */
class SendSpec extends UnitSpec {

  import SEND._
  import scala.language.postfixOps

  "Adding a template and some addresses" should {
    "create an EmailOps" in {
      implicit val emailConfiguration = EmailConfiguration("host", 25, "username", "password",
        From("email", "name"), Some(List("test@gov.co.uk")))
      val template = Contents("<h1>Email</h1>", "text email")
      val receivers = List("test@gov.co.uk")

      val email = SEND email template withSubject "Some Subject" to receivers

      email shouldBe a [SEND.Email]


      mailtoOps(email) shouldBe a [SEND.WhiteListEmailOps]
    }
  }

}
