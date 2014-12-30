//package email
//
//import helpers.UnitSpec
//import utils.helpers.Config
//import composition.WithApplication
//
//
///**
// * Created by gerasimosarvanitis on 04/12/2014.
// */
//class SendSpec extends UnitSpec {
//
//  import SEND._
//  import scala.language.postfixOps
//
//  implicit val emailConfiguration = EmailConfiguration("port", 25, "username", "password",
//                                    From("donotreplypronline@dvla.gsi.gov.uk", "DO-NOT-REPLY"),
//                                    Some(List("@valtech.co.uk","@dvla.gsi.gov.uk","@digital.dvla.gov.uk")))
//
//  "whitelist" should {
//    "return true if an email belongs to this list" in new WithApplication {
//      val receivers = List("test@valtech.co.uk")
//      SEND.isWhiteListed(receivers) shouldEqual true
//    }
//    "return false if an email doesn't belong to this list" in new WithApplication {
//      val receivers = List("test@gmail.com")
//      SEND.isWhiteListed(receivers) shouldEqual false
//    }
//  }
//
//  "add people to the email" should {
//    "add people if it is a list" in {
//      val template = Contents("<h1>Email</h1>", "text email")
//      val receivers = List("test@valtech.co.uk")
//      val email = SEND email template withSubject ""
//      val appendedEmail = email.to(receivers)
//
//      appendedEmail.toPeople.getOrElse(List()).toArray should equal (receivers.toArray)
//    }
//
//    "add people if we add one by one" in {
//      val template = Contents("<h1>Email</h1>", "text email")
//      val person1 = "test1@valtech.co.uk"
//      val person2 = "test2@valtech.co.uk"
//
//      val email = SEND email template withSubject ""
//      val appendedEmail = email to (person1, person2)
//
//      appendedEmail.toPeople.getOrElse(List()).toArray should equal (Array(person1, person2))
//    }
//
//    "add people ic cc" in {
//      val template = Contents("<h1>Email</h1>", "text email")
//      val person1 = "test1@valtech.co.uk"
//      val person2 = "test2@valtech.co.uk"
//
//      val email = SEND email template withSubject "" cc (person1, person2)
//
//      email.ccPeople.getOrElse(List()).toArray should equal (Array(person1, person2))
//    }
//  }
//
//  "Adding a template and some addresses" should {
//    "create an WhiteList if the user belongs to the whitelist" in new WithApplication {
//      val template = Contents("<h1>Email</h1>", "text email")
//      val receivers = List("test@valtech.co.uk")
//
//      val email = SEND email template withSubject "Some Subject" to receivers
//
//      email shouldBe a [SEND.Email]
//
//      mailtoOps(email) shouldBe a [SEND.WhiteListEmailOps]
//    }
//
//    "create a NoEmailOps if the email doesn't have any senders" in new WithApplication{
//      val template = Contents("<h1>Email</h1>", "text email")
//
//      val email = SEND email template withSubject "Some Subject"
//
//      mailtoOps(email) shouldBe NoEmailOps
//    }
//
//    "send an email if the emailis ok" in new WithApplication {
//      val template = Contents("<h1>Email</h1>", "text email")
//      val receivers = List("makis.arvin@gmail.com")
//
//      val email = SEND email template withSubject "Some Subject" to receivers
//
//      mailtoOps(email) shouldBe a [SEND.SmtpEmailOps]
//    }
//  }
//
//}
