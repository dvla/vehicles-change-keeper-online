package helpers.changekeeper

import helpers.changekeeper.CookieFactoryForUnitSpecs.VehicleLookupFailureResponseCode
import models.{PrivateKeeperDetailsFormModel, VehicleLookupFormModel}
import models.VehicleLookupFormModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}
import org.openqa.selenium.WebDriver
import pages.changekeeper.PrivateKeeperDetailsPage.FirstNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.LastNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.DayDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.MonthDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.YearDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.PostcodeValid
import pages.changekeeper.PrivateKeeperDetailsPage.ModelValid
import pages.changekeeper.PrivateKeeperDetailsPage.EmailValid
import pages.changekeeper.PrivateKeeperDetailsPage.DriverNumberValid
import play.api.Play
import play.api.Play.current
import play.api.libs.json.{Json, Writes}
import uk.gov.dvla.vehicles.presentation.common
import common.controllers.AlternateLanguages.{CyId, EnId}
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel.BruteForcePreventionViewModelCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel.VehicleLookupDetailsCacheKey
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{RegistrationNumberValid, VehicleMakeValid, VehicleModelValid}
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import org.openqa.selenium.Cookie
import org.joda.time.LocalDate
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts

object CookieFactoryForUISpecs {
  private def addCookie[A](key: String, value: A)(implicit tjs: Writes[A], webDriver: WebDriver): Unit = {
    val valueAsString = Json.toJson(value).toString()
    val manage = webDriver.manage()
    val cookie = new Cookie(key, valueAsString)
    manage.addCookie(cookie)
  }

  def withLanguageCy()(implicit webDriver: WebDriver) = {
    val key = Play.langCookieName
    val value = CyId
    addCookie(key, value)
    this
  }

  def withLanguageEn()(implicit webDriver: WebDriver) = {
    val key = Play.langCookieName
    val value = EnId
    addCookie(key, value)
    this
  }

  def vehicleAndKeeperDetails(registrationNumber: String = RegistrationNumberValid,
                              vehicleMake: Option[String] = Some(VehicleMakeValid),
                              vehicleModel: Option[String] = Some(VehicleModelValid),
                              title: Option[String] = None,
                              firstName: Option[String] = None,
                              lastName: Option[String] = None,
                              address: Option[AddressModel] = None)(implicit webDriver: WebDriver) = {
    val key = VehicleAndKeeperLookupDetailsCacheKey
    val value = VehicleAndKeeperDetailsModel(
      registrationNumber = registrationNumber,
      make = vehicleMake,
      model = vehicleModel,
      title = title,
      firstName = firstName,
      lastName = lastName,
      address = address
    )
    addCookie(key, value)
    this
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString)
                                   (implicit webDriver: WebDriver) = {
    val key = BruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology
    )
    addCookie(key, value)
    this
  }

  def vehicleLookupFormModel(referenceNumber: String = FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid,
                             vehicleSoldTo: String = VehicleSoldTo_Private)(implicit webDriver: WebDriver) = {
    val key = VehicleLookupFormModelCacheKey
    val value = VehicleLookupFormModel(referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      vehicleSoldTo = vehicleSoldTo)
    addCookie(key, value)
    this
  }

  def vehicleLookupResponseCode(responseCode: String = VehicleLookupFailureResponseCode)
                               (implicit webDriver: WebDriver) = {
    val key = VehicleLookupResponseCodeCacheKey
    val value = responseCode
    addCookie(key, value)
    this
  }

  def vehicleDetails(registrationNumber: String = RegistrationNumberValid,
                     vehicleMake: String = VehicleMakeValid,
                     vehicleModel: String = ModelValid,
                     disposeFlag: Boolean = false)(implicit webDriver: WebDriver) = {
    val key = VehicleLookupDetailsCacheKey
    val value = VehicleDetailsModel(
      registrationNumber = registrationNumber,
      vehicleMake,
      vehicleModel,
      disposeFlag)
    addCookie(key, value)
    this
  }

  def privateKeeperDetailsModel(title: TitleType = TitleType(1, ""),
                                firstName: String = FirstNameValid,
                                lastName: String = LastNameValid,
                                dateOfBirth: Option[LocalDate] = Some(
                                  new LocalDate(
                                    YearDateOfBirthValid.toInt,
                                    MonthDateOfBirthValid.toInt,
                                    DayDateOfBirthValid.toInt
                                  )
                                ),
                                email: Option[String] = Some(EmailValid),
                                driverNumber: Option[String] = Some(DriverNumberValid),
                                postcode: String = PostcodeValid
                                 )(implicit webDriver: WebDriver) = {
    val key = PrivateKeeperDetailsCacheKey
    val value = PrivateKeeperDetailsFormModel(
      title = title,
      firstName = firstName,
      lastName = lastName,
      dateOfBirth,
      email = email,
      driverNumber = driverNumber,
      postcode = postcode
    )
    addCookie(key, value)
    this
  }
}