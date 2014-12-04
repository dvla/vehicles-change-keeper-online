package email

import org.apache.commons.mail.{Email => ApacheEmail, HtmlEmail}


/**
 * A simple service to send an email, leveraging the apache commons email library.
 *
 * Usage:
 *  val from = EmailAddress("dvla@co.uk", "DVLA Department of..")
 *  implicit configuration = SEND.EmailConfiguration("host", 25, "username", "passwd", from)
 *  SEND email 'message withSubject 'subject to 'peopleList cc 'ccList send
 *
 * Created by gerasimosarvanitis on 03/12/2014.
 */



object SEND {
  import scala.language.implicitConversions
  import scala.language.postfixOps
  import scala.language.reflectiveCalls

  case class From(email: String, name: String)
  case class EmailConfiguration(host: String, port: Int, username: String, password: String, from: From)

  case class Contents(htmlMessage: String, plainMessage: String)

  case class Email(message: Contents, subject: String,
                   toPeople: Option[List[String]] = None,
                   ccPeople: Option[List[String]] = None) {

    def to (people: List[String]): Email = this.copy(toPeople = Some(people))
    def cc (people: List[String]): Email = this.copy(ccPeople = Some(people))


  }

  case class EmailOps(email: Email) {

    def send(implicit config: EmailConfiguration) = {
//      s"""Got email with contents: (${email.subject} - ${email.message} ) to be sent to ${email.toPeople.mkString(" ")}
//       |with cc (${email.ccPeople.mkString(" ")}) and configuration: ${config.port} ${config.username}""".stripMargin

      def createHtml(config: EmailConfiguration): HtmlEmail = {
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

      populateReceivers(email)(createHtml(config)).
        setHtmlMsg(email.message.htmlMessage).
        setTextMsg(email.message.plainMessage).
        send()

    }

  }

  implicit def mailtoOps (mail: Email): EmailOps = EmailOps(mail)


  def email(message: Contents) = new { def withSubject(subject: String) = Email(message, subject) }

  //def test = SEND email "template" withSubject "Subject" to List.empty[String] cc List.empty[String] send SEND.EmailConfiguration("port", "username")

}


