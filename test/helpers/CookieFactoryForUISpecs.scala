package helpers

import CookieFactoryForUnitSpecs.VehicleLookupFailureResponseCode

import models.BusinessKeeperDetailsFormModel.BusinessKeeperDetailsCacheKey
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import models.BusinessKeeperDetailsFormModel
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmResponseModel
import models.NewKeeperDetailsViewModel
import models.PrivateKeeperDetailsFormModel
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.{VehicleLookupFormModelCacheKey, VehicleLookupResponseCodeCacheKey}
import org.joda.time.{DateTime, LocalDate}
import org.openqa.selenium.{Cookie, WebDriver}
import pages.changekeeper.BusinessKeeperDetailsPage.{BusinessNameValid, FleetNumberValid}
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue
import pages.changekeeper.CompleteAndConfirmPage.DayDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.MileageValid
import pages.changekeeper.CompleteAndConfirmPage.MonthDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.YearDateOfSaleValid
import pages.changekeeper.PrivateKeeperDetailsPage.DayDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.DriverNumberValid
import pages.changekeeper.PrivateKeeperDetailsPage.EmailValid
import pages.changekeeper.PrivateKeeperDetailsPage.FirstNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.LastNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.ModelValid
import pages.changekeeper.PrivateKeeperDetailsPage.MonthDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.PostcodeValid
import pages.changekeeper.PrivateKeeperDetailsPage.YearDateOfBirthValid
import play.api.Play
import play.api.Play.current
import play.api.libs.json.{Json, Writes}
import uk.gov.dvla.vehicles.presentation.common.controllers.AlternateLanguages.{CyId, EnId}
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel.BruteForcePreventionViewModelCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.AddressModel
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel
import uk.gov.dvla.vehicles.presentation.common.model.VehicleDetailsModel.VehicleLookupDetailsCacheKey
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.FakeAddressLookupService.{BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid}
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.TransactionIdValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.TransactionTimestampValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleMakeValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleModelValid
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

  def privateKeeperDetails(title: TitleType = TitleType(1, ""),
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

  def businessKeeperDetails(fleetNumber: Option[String] = Some(FleetNumberValid),
                            businessName: String = BusinessNameValid,
                            email: Option[String] = Some(EmailValid),
                            postcode: String = PostcodeValid)(implicit webDriver: WebDriver) = {
    val key = BusinessKeeperDetailsCacheKey
    val value = BusinessKeeperDetailsFormModel(
      fleetNumber = fleetNumber,
      businessName = businessName,
      email = email,
      postcode = postcode
    )
    addCookie(key, value)
    this
  }

  def newKeeperDetailsModel(title: Option[TitleType] = None,
                            firstName: Option[String] = None,
                            lastName: Option[String] = None,
                            dateOfBirth: Option[LocalDate] = None,
                            driverNumber: Option[String] = None,
                            businessName: Option[String] = None,
                            fleetNumber: Option[String] = None,
                            email: Option[String] = None,
                            isBusinessKeeper: Boolean = false,
                            uprn: Option[Long] = None,
                            buildingNameOrNumber: String = BuildingNameOrNumberValid,
                            line2: String = Line2Valid,
                            line3: String = Line3Valid,
                            postTown: String = PostTownValid,
                            postcode: String = PostcodeValid)(implicit webDriver: WebDriver) = {
    val key = NewKeeperDetailsCacheKey
    val value = NewKeeperDetailsViewModel(
      title = title,
      firstName = firstName,
      lastName = lastName,
      dateOfBirth = dateOfBirth,
      driverNumber = driverNumber,
      businessName = businessName,
      fleetNumber = fleetNumber,
      address = AddressModel(uprn = uprn, address = Seq(buildingNameOrNumber, line2, line3, postTown, postcode)),
      email = email,
      isBusinessKeeper = isBusinessKeeper,
      displayName = if (businessName == None) firstName + " " + lastName
      else businessName.getOrElse("")
    )
    addCookie(key, value)
    this
  }

  def allowGoingToCompleteAndConfirmPageCookie()(implicit webDriver: WebDriver) = {
    addCookie(CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey, "")
  }

  def completeAndConfirmResponseModelModel(id: String = TransactionIdValid,
                                           timestamp: DateTime = TransactionTimestampValid)(implicit webDriver: WebDriver) = {
    val key = ChangeKeeperCompletionResponseCacheKey
    val value = CompleteAndConfirmResponseModel(id, timestamp)
    addCookie(key, value)
    this
  }

  def completeAndConfirmDetails(mileage: Option[Int] = Some(MileageValid.toInt),
                                dateOfSale: LocalDate = new LocalDate(
                                  YearDateOfSaleValid.toInt,
                                  MonthDateOfSaleValid.toInt,
                                  DayDateOfSaleValid.toInt),
                                consent: String = ConsentTrue)(implicit webDriver: WebDriver) = {
    val key = CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
    val value = CompleteAndConfirmFormModel(
      mileage,
      dateOfSale,
      consent
    )
    addCookie(key, value)
    this
  }
}
