package helpers.changekeeper

import helpers.changekeeper.CookieFactoryForUnitSpecs._
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.K2KCacheKeyPrefix.CookiePrefix
import models.{CompleteAndConfirmFormModel, CompleteAndConfirmResponseModel, DateOfSaleFormModel, SellerEmailModel, VehicleLookupFormModel}
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.joda.time.{DateTime, LocalDate}
import org.openqa.selenium.{Cookie, WebDriver}
import pages.changekeeper.BusinessKeeperDetailsPage.{BusinessNameValid, FleetNumberValid}
import pages.changekeeper.CompleteAndConfirmPage.ConsentTrue
import pages.changekeeper.DateOfSalePage.{DayDateOfSaleValid, MileageValid, MonthDateOfSaleValid, YearDateOfSaleValid}
import pages.changekeeper.PrivateKeeperDetailsPage.{DayDateOfBirthValid, DriverNumberValid, EmailValid, FirstNameValid, LastNameValid, MonthDateOfBirthValid, PostcodeValid, YearDateOfBirthValid}
import play.api.Play
import play.api.Play.current
import play.api.libs.json.{Json, Writes}
import uk.gov.dvla.vehicles.presentation.common
import uk.gov.dvla.vehicles.presentation.common.controllers.AlternateLanguages.{CyId, EnId}
import uk.gov.dvla.vehicles.presentation.common.mappings.TitleType
import uk.gov.dvla.vehicles.presentation.common.model.BruteForcePreventionModel.bruteForcePreventionViewModelCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.MicroserviceResponseModel.MsResponseCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.model.{AddressModel, BruteForcePreventionModel, BusinessKeeperDetailsFormModel, MicroserviceResponseModel, NewKeeperDetailsViewModel, PrivateKeeperDetailsFormModel, VehicleAndKeeperDetailsModel}
import uk.gov.dvla.vehicles.presentation.common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.common.MicroserviceResponse
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.FakeAddressLookupService.{BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid}
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{RegistrationNumberValid, TransactionIdValid, TransactionTimestampValid, VehicleMakeValid, VehicleModelValid}
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

  def withIdentifier(id: String)(implicit webDriver: WebDriver) = {
    webDriver.manage().addCookie(new Cookie(models.IdentifierCacheKey, id))
    this
  }

  def vehicleAndKeeperDetails(registrationNumber: String = RegistrationNumberValid,
                              vehicleMake: Option[String] = Some(VehicleMakeValid),
                              vehicleModel: Option[String] = Some(VehicleModelValid),
                              title: Option[String] = None,
                              firstName: Option[String] = None,
                              lastName: Option[String] = None,
                              address: Option[AddressModel] = None)(implicit webDriver: WebDriver) = {
    val key = vehicleAndKeeperLookupDetailsCacheKey
    val value = VehicleAndKeeperDetailsModel(
      registrationNumber = registrationNumber,
      make = vehicleMake,
      model = vehicleModel,
      title = title,
      firstName = firstName,
      lastName = lastName,
      address = address,
      keeperEndDate = None,
      keeperChangeDate = None,
      disposeFlag = None,
      suppressedV5Flag = None
    )
    addCookie(key, value)
    this
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString)
                                   (implicit webDriver: WebDriver) = {
    val key = bruteForcePreventionViewModelCacheKey
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

  def vehicleLookupResponse(responseMessage: String = VehicleLookupFailureResponseMessage)
                           (implicit webDriver: WebDriver) = {
    val key = MsResponseCacheKey
    val value = MicroserviceResponseModel(MicroserviceResponse("", responseMessage))
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
    val key = privateKeeperDetailsCacheKey
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
    val key = businessKeeperDetailsCacheKey
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
    val key = newKeeperDetailsCacheKey
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
      displayName = if (businessName.isEmpty) firstName + " " + lastName
      else businessName.getOrElse("")
    )
    addCookie(key, value)
    this
  }

  def sellerEmailModel(email: Option[String] = None)(implicit webDriver: WebDriver) = {
    addCookie(SellerEmailModel.SellerEmailModelCacheKey, email)
    this
  }

  def allowGoingToCompleteAndConfirmPageCookie()(implicit webDriver: WebDriver) = {
    addCookie(CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey, "true")
  }

  def completeAndConfirmResponseModelModel(id: String = TransactionIdValid,
                                           timestamp: DateTime = TransactionTimestampValid)
                                          (implicit webDriver: WebDriver) = {
    val key = ChangeKeeperCompletionResponseCacheKey
    val value = CompleteAndConfirmResponseModel(id, timestamp)
    addCookie(key, value)
    this
  }

  def dateOfSaleDetails(mileage: Option[Int] = Some(MileageValid.toInt),
                        dateOfSale: LocalDate = new LocalDate(
                          YearDateOfSaleValid.toInt,
                          MonthDateOfSaleValid.toInt,
                          DayDateOfSaleValid.toInt
                        ))(implicit webDriver: WebDriver) = {
    addCookie(DateOfSaleFormModel.DateOfSaleCacheKey, DateOfSaleFormModel(mileage, dateOfSale))
    this
  }

  def completeAndConfirmDetails(regRight: String = ConsentTrue, consent: String = ConsentTrue)(implicit webDriver: WebDriver) = {
    val key = CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
    val value = CompleteAndConfirmFormModel(
      regRight,
      consent
    )
    addCookie(key, value)
    this
  }
}
