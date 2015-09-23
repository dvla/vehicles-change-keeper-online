package pages.changekeeper

import org.openqa.selenium.WebDriver
import org.scalatest.Matchers
import uk.gov.dvla.vehicles.presentation.common
import common.helpers.webbrowser.WebDriverFactory
import common.helpers.webbrowser.Page
import common.mappings.Email.{EmailId => EmailEnterId, EmailVerifyId}
import common.mappings.OptionalToggle.{Visible, Invisible}
import common.mappings.TitlePickerString.OtherTitleRadioValue
import common.model.PrivateKeeperDetailsFormModel.Form.DateOfBirthId
import common.model.PrivateKeeperDetailsFormModel.Form.DriverNumberId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailId
import common.model.PrivateKeeperDetailsFormModel.Form.EmailOptionId
import common.model.PrivateKeeperDetailsFormModel.Form.FirstNameId
import common.model.PrivateKeeperDetailsFormModel.Form.LastNameId
import common.model.PrivateKeeperDetailsFormModel.Form.PostcodeId
import common.model.PrivateKeeperDetailsFormModel.Form.TitleId
import views.changekeeper.PrivateKeeperDetails.{BackId, SubmitId}
import org.scalatest.selenium.WebBrowser.EmailField
import org.scalatest.selenium.WebBrowser.emailField
import org.scalatest.selenium.WebBrowser.TextField
import org.scalatest.selenium.WebBrowser.textField
import org.scalatest.selenium.WebBrowser.TelField
import org.scalatest.selenium.WebBrowser.telField
import org.scalatest.selenium.WebBrowser.RadioButton
import org.scalatest.selenium.WebBrowser.radioButton
import org.scalatest.selenium.WebBrowser.click
import org.scalatest.selenium.WebBrowser.go
import org.scalatest.selenium.WebBrowser.find
import org.scalatest.selenium.WebBrowser.id
import org.scalatest.selenium.WebBrowser.Element
import org.scalatest.selenium.WebBrowser.tagName

object PrivateKeeperDetailsPage extends Page with Matchers {
  final val address = buildAppUrl("private-keeper-details")
  override val url: String = WebDriverFactory.testUrl + address.substring(1)
  final override val title: String = "Enter new keeper details"

  final val TitleInvalid = "other"
  final val FirstNameValid = "fn"
  final val FirstNameInvalid = ""
  final val LastNameValid = "TestLastName"
  final val LastNameInvalid = ""
  final val EmailValid = "my@email.com"
  final val EmailInvalid = "no_at_symbol.com"
  final val VehicleMakeValid = "Audi"
  final val ModelValid = "A6"
  final val DriverNumberValid = "ABCD9711215EFLGH"
  final val DriverNumberInvalid = "A"
  final val DayDateOfBirthValid = "24"
  final val MonthDateOfBirthValid = "12"
  final val YearDateOfBirthValid = "1920"
  final val PostcodeValid = "QQ99QQ"
  final val NoPostcodeFound = "XX99XX"
  final val PostcodeInvalid = "XX99X"

  def back(implicit driver: WebDriver): Element = find(id(BackId)).get

  def next(implicit driver: WebDriver): Element = find(id(SubmitId)).get

  def mr(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("mr")}"))
  def miss(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("miss")}"))
  def mrs(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_${titleType("mrs")}"))
  def other(implicit driver: WebDriver) = radioButton(id(s"${TitleId}_titleOption_$OtherTitleRadioValue"))
  def otherText(implicit driver: WebDriver) = textField(id(s"${TitleId}_titleText"))

  def emailVisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Visible"))

  def emailInvisible(implicit driver: WebDriver): RadioButton =
    radioButton(id(s"${EmailOptionId}_$Invisible"))

  def emailTextBox(implicit driver: WebDriver): EmailField = emailField(id(s"${EmailId}_$EmailEnterId"))

  def emailConfirmTextBox(implicit driver: WebDriver): EmailField = emailField(id(s"${EmailId}_$EmailVerifyId"))

  def driverNumberTextBox(implicit driver: WebDriver): TextField = textField(id(DriverNumberId))

  def firstNameTextBox(implicit driver: WebDriver): TextField = textField(id(FirstNameId))

  def lastNameTextBox(implicit driver: WebDriver): TextField = textField(id(LastNameId))

  def dayDateOfBirthTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfBirthId" + "_day"))

  def monthDateOfBirthTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfBirthId" + "_month"))

  def yearDateOfBirthTextBox(implicit driver: WebDriver): TelField = telField(id(s"$DateOfBirthId" + "_year"))

  def postcodeTextBox(implicit driver: WebDriver): TextField = textField(id(PostcodeId))

  private def titleRadioButtons(implicit driver: WebDriver) = Seq(mr, miss, mrs, other)

  def errorTextForTitle(text:String)(implicit driver: WebDriver):Boolean= find(tagName("body")).get.text.contains(text)

  def selectTitle(title: String)(implicit driver: WebDriver): Unit = {
    titleRadioButtons.find(_.underlying.getAttribute("id") endsWith titleType(title)).fold {
      click on other
      otherText
        .value = title
    } (click on _)
  }

  def assertTitleSelected(title: String)(implicit driver: WebDriver): Unit = {
    titleRadioButtons.find(_.underlying.getAttribute("id") endsWith titleType(title))
      .fold(throw new Exception) ( _.isSelected should equal(true))
    titleRadioButtons.filterNot(_.underlying.getAttribute("id") endsWith titleType(title))
      .foreach(_.isSelected should equal(false))
  }

  def assertNoTitleSelected()(implicit driver: WebDriver): Unit = {
    titleRadioButtons.foreach(_.isSelected should equal(false))
  }

  def navigate(title: String = "mr",
                firstName: String = FirstNameValid,
                lastName: String = LastNameValid,
                dayDateOfBirth: String = DayDateOfBirthValid,
                monthDateOfBirth: String = MonthDateOfBirthValid,
                yearDateOfBirth: String = YearDateOfBirthValid,
                email: String = EmailValid,
                driverNumber: String = DriverNumberValid,
                postcode: String = PostcodeValid)(implicit driver: WebDriver) = {
    go to PrivateKeeperDetailsPage

    selectTitle(title)
    firstNameTextBox.value = firstName
    lastNameTextBox.value = lastName
    dayDateOfBirthTextBox.value = dayDateOfBirth
    monthDateOfBirthTextBox.value = monthDateOfBirth
    yearDateOfBirthTextBox.value = yearDateOfBirth
    click on emailVisible
    emailTextBox.value = email
    emailConfirmTextBox.value = email
    driverNumberTextBox.value = driverNumber
    postcodeTextBox.value = postcode

    click on next
  }

  def submitPostcodeWithoutAddresses(implicit driver: WebDriver) = {
    navigate(postcode = NoPostcodeFound)
  }

  private def titleType(title: String): String = title match {
    case "mr" => "1"
    case "mrs" => "2"
    case "miss" => "3"
    case "other" => "4"
    case _ => "unknown"
  }
}
