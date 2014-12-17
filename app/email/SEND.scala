package email

import org.apache.commons.mail.{Email => ApacheEmail, EmailException, HtmlEmail}
import play.api.Logger


/**
 * A simple service to send an email, leveraging the apache commons email library.
 *
 * Usage:
 *  val from = EmailAddress("dvla@co.uk", "DVLA Department of..")
 *  implicit configuration = SEND.EmailConfiguration("host", 25, "username", "passwd", from, None)
 *
 *  SEND email 'message withSubject 'subject to 'peopleList cc 'ccList send
 *
 * Created by gerasimosarvanitis on 03/12/2014.
 */
object SEND {
  import scala.language.implicitConversions
  import scala.language.postfixOps
  import scala.language.reflectiveCalls

  case class From(email: String, name: String)
  case class EmailConfiguration(host: String, port: Int, username: String, password: String,
                                from: From, whiteList: Option[List[String]])

  case class Contents(htmlMessage: String, plainMessage: String)

  case class Email(message: Contents, subject: String,
                   toPeople: Option[List[String]] = None,
                   ccPeople: Option[List[String]] = None) {

    def to (people: String*): Email = to(people.toList)
    def to (people: List[String]): Email = toPeople match {
      case None => this.copy(toPeople = Some(people.toList))
      case Some(_) => this.copy(toPeople = toPeople.map( _ ++ people.toList))
    }

    def cc (people: String*): Email = cc(people.toList)
    def cc (people: List[String]): Email = toPeople match {
      case None => this.copy(ccPeople = Some(people.toList))
      case Some(_) => this.copy(ccPeople = ccPeople.map( _ ++ people.toList))
    }


  }

  /** Generic trait to represent the Email Service */
  sealed trait EmailOps {
    def send(implicit config: EmailConfiguration): Unit
  }

  /** A dummy email service, to send the white listed emails. */
  case class WhiteListEmailOps(email: Email) extends EmailOps {
    def send(implicit config: EmailConfiguration) = {
      val message = s"""Got email with contents: (${email.subject} - ${email.message} ) to be sent to ${email.toPeople.mkString(" ")}
         ||with cc (${email.ccPeople.mkString(" ")}) and configuration: ${config.port} ${config.username} from email
         |${config.from.email}. Receiver was in whitelist""".stripMargin

      Logger.info(message)
    }
  }
  /** A no-ops email service that denotes an error in the email */
  object NoEmailOps extends EmailOps {
    def send(implicit config: EmailConfiguration) = Logger.info("The email is incomplete. you cannot send that")

  }

  /** An smtp email service. Currently implemented using the Apache commons email library */
  case class SmtpEmailOps(email: Email) extends EmailOps{

    def send(implicit config: EmailConfiguration) = {

      def createEmail(config: EmailConfiguration): HtmlEmail = {
        val htmlEmail = new HtmlEmail
        //configure server
        htmlEmail.setHostName(config.host)
        htmlEmail.setSmtpPort(config.port)
        htmlEmail.setAuthentication(config.username, config.password)

        htmlEmail.setFrom(config.from.email, config.from.name)

        htmlEmail

      }

      def populateReceivers(email: Email)(htmlEmail: HtmlEmail) = {

        def populate(f: String => ApacheEmail)(lst: Option[List[String]]) = for {
          sendList <- lst
          address <- sendList
        } f(address)

        populate(htmlEmail.addTo)(email.toPeople)
        populate(htmlEmail.addCc)(email.ccPeople)

        htmlEmail

      }
      try {
        populateReceivers(email)(createEmail(config)).
          setHtmlMsg(email.message.htmlMessage).
          setTextMsg(email.message.plainMessage).
          setSubject(email.subject).
          send()
      } catch {
        case ex: EmailException => Logger.error(s"""Failed to send email for ${email.toPeople.mkString(" ")} reason was ${ex.getMessage}""")
      }

    }

  }

  /**
   * Validation method that will return the correct service implementation depending on the email.
   * @param mail the email to send
   * @param configuration the configuration needed
   * @return an appropriate instance of an email.
   */
  implicit def mailtoOps (mail: Email)(implicit configuration: EmailConfiguration): EmailOps = mail match {
    case Email(message, _, Some(toPeople), _) if isWhiteListed(toPeople) => WhiteListEmailOps(mail)
    case Email(message, _, Some(toPeople), _)                            => SmtpEmailOps(mail)
    case _                                                               => NoEmailOps
  }

  /**
   * private method to decide if the email has a white listed address. In case there is a white listed address then the
   * method will return true.
   */
  def isWhiteListed(addresses: List[String])(implicit configuration: EmailConfiguration): Boolean = (for {
    address <- addresses
    whiteList <- configuration.whiteList
  } yield whiteList.filter((domain: String) => address.endsWith(domain))).flatten match {
    case List() => false
    case _ => true
  }

  /**
   * Main entry point for the send email.
   * @param message the contents of the email.
   * @return an instance if the email message.
   */
  def email(message: Contents) = new { def withSubject(subject: String) = Email(message, subject) }

}


