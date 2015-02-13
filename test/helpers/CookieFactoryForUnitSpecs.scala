package helpers

import composition.TestComposition
import models.NewKeeperDetailsViewModel.NewKeeperDetailsCacheKey
import pages.changekeeper.BusinessKeeperDetailsPage.{FleetNumberValid, BusinessNameValid}
import pages.changekeeper.HelpPage
import play.api.libs.json.{Json, Writes}
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmResponseModel
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.K2KCacheKeyPrefix.CookiePrefix
import models.HelpCacheKey
import models.NewKeeperChooseYourAddressFormModel
import models.NewKeeperDetailsViewModel
import models.NewKeeperEnterAddressManuallyFormModel
import models.NewKeeperEnterAddressManuallyFormModel.NewKeeperEnterAddressManuallyCacheKey
import models.PrivateKeeperDetailsFormModel
import models.PrivateKeeperDetailsFormModel.PrivateKeeperDetailsCacheKey
import models.SeenCookieMessageCacheKey
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.{VehicleLookupResponseCodeCacheKey, VehicleLookupFormModelCacheKey}
import org.joda.time.{DateTime, LocalDate}
import pages.changekeeper.CompleteAndConfirmPage.MileageValid
import pages.changekeeper.CompleteAndConfirmPage.DayDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.MonthDateOfSaleValid
import pages.changekeeper.CompleteAndConfirmPage.YearDateOfSaleValid
import pages.changekeeper.PrivateKeeperDetailsPage.{FirstNameValid, LastNameValid, EmailValid, DriverNumberValid}
import pages.changekeeper.PrivateKeeperDetailsPage.{DayDateOfBirthValid, MonthDateOfBirthValid, YearDateOfBirthValid, PostcodeValid}
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{ClearTextClientSideSession, ClientSideSessionFactory, CookieFlags}
import common.mappings.TitleType
import common.model.{BruteForcePreventionModel, AddressModel, VehicleAndKeeperDetailsModel}
import common.model.BruteForcePreventionModel.BruteForcePreventionViewModelCacheKey
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.BusinessKeeperDetailsFormModel
import common.model.VehicleAndKeeperDetailsModel.VehicleAndKeeperLookupDetailsCacheKey
import common.views.models.{AddressLinesViewModel, AddressAndPostcodeViewModel}
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.FakeAddressLookupService.{BuildingNameOrNumberValid, Line2Valid, Line3Valid, PostTownValid}
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.UprnValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleMakeValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleModelValid
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{TransactionIdValid, TransactionTimestampValid}

object CookieFactoryForUnitSpecs extends TestComposition {

  implicit private val cookieFlags = injector.getInstance(classOf[CookieFlags])
  final val TrackingIdValue = "trackingId"
  final val KeeperEmail = "abc@def.com"
  final val SeenCookieTrue = "yes"
  final val ConsentTrue = "true"
  final val VehicleLookupFailureResponseCode = "400 - vehiclelookupfailure"
  private val session = new ClearTextClientSideSession(TrackingIdValue)

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
  }

  def seenCookieMessage(): Cookie = {
    val key = SeenCookieMessageCacheKey
    val value = SeenCookieTrue
    createCookie(key, value)
  }

  def trackingIdModel(value: String = TrackingIdValue): Cookie = {
    createCookie(ClientSideSessionFactory.TrackingIdCookieName, value)
  }

/*
  def microServiceError(origin: String = VehicleLookupPage.address): Cookie = {
    val key = MicroServiceErrorRefererCacheKey
    val value = origin
    createCookie(key, value)
  }
*/

  def help(origin: String = HelpPage.address): Cookie = {
    val key = HelpCacheKey
    val value = origin
    createCookie(key, value)
  }

  def vehicleLookupFormModel(referenceNumber: String = ReferenceNumberValid,
                             registrationNumber: String = RegistrationNumberValid,
                             vehicleSoldTo: String = VehicleSoldTo_Private): Cookie = {
    val key = VehicleLookupFormModelCacheKey
    val value = VehicleLookupFormModel(
      referenceNumber = referenceNumber,
      registrationNumber = registrationNumber,
      vehicleSoldTo = vehicleSoldTo
    )
    createCookie(key, value)
  }

  def vehicleAndKeeperDetailsModel(registrationNumber: String = RegistrationNumberValid,
                                   vehicleMake: Option[String] = Some(VehicleMakeValid),
                                   vehicleModel: Option[String] = Some(VehicleModelValid),
                                   title: Option[String] = None,
                                   firstName: Option[String] = None,
                                   lastName: Option[String] = None,
                                   address: Option[AddressModel] = None): Cookie = {
    val key = VehicleAndKeeperLookupDetailsCacheKey
    val value = VehicleAndKeeperDetailsModel(
      registrationNumber = registrationNumber,
      make = vehicleMake,
      model = vehicleModel,
      title = title,
      firstName = firstName,
      lastName = lastName,
      address = address,
      keeperEndDate = None,
      disposeFlag = None,
      suppressedV5Flag = None
    )
    createCookie(key, value)
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
                                ): Cookie = {
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
    createCookie(key, value)
  }

  def businessKeeperDetailsModel(fleetNumber: Option[String] = Some(FleetNumberValid),
                                 businessName: String = BusinessNameValid,
                                 email: Option[String] = Some(EmailValid),
                                 postcode: String = PostcodeValid) : Cookie = {
    val key = businessKeeperDetailsCacheKey
    val value = BusinessKeeperDetailsFormModel(
      fleetNumber = fleetNumber,
      businessName = businessName,
      email = email,
      postcode = postcode
    )
    createCookie(key, value)
  }

  def newKeeperChooseYourAddressUseUprn(uprnSelected: String = UprnValid.toString): Cookie = {
    val key = NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
    val value = NewKeeperChooseYourAddressFormModel(uprnSelected = uprnSelected)
    createCookie(key, value)
  }

  def newKeeperChooseYourAddress(uprnSelected: String = "0"): Cookie = {
    val key = NewKeeperChooseYourAddressFormModel.NewKeeperChooseYourAddressCacheKey
    val value = NewKeeperChooseYourAddressFormModel(uprnSelected = uprnSelected)
    createCookie(key, value)
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
                            postcode: String = PostcodeValid): Cookie = {
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
    createCookie(key, value)
  }

  def newKeeperEnterAddressManually(): Cookie = {
    val key = NewKeeperEnterAddressManuallyCacheKey
    val value = NewKeeperEnterAddressManuallyFormModel(
      addressAndPostcodeModel = AddressAndPostcodeViewModel(
        addressLinesModel = AddressLinesViewModel(
          buildingNameOrNumber = BuildingNameOrNumberValid,
          line2 = Some(Line2Valid),
          line3 = Some(Line3Valid),
          postTown = PostTownValid
        )
      )
    )
    createCookie(key, value)
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString): Cookie = {
    val key = BruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology = dateTimeISOChronology
    )
    createCookie(key, value)
  }

  def vehicleLookupResponseCode(responseCode: String = VehicleLookupFailureResponseCode): Cookie =
    createCookie(VehicleLookupResponseCodeCacheKey, responseCode)

  def allowGoingToCompleteAndConfirm(): Cookie =
    createCookie(CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey, "")

  def completeAndConfirmModel(mileage: Option[Int] = Some(MileageValid.toInt),
                              dateOfSale: LocalDate = new LocalDate(
                                YearDateOfSaleValid.toInt,
                                MonthDateOfSaleValid.toInt,
                                DayDateOfSaleValid.toInt),
                              consent: String = ConsentTrue): Cookie = {
    val key = CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
    val value = CompleteAndConfirmFormModel(
      mileage,
      dateOfSale,
      consent
    )
    createCookie(key, value)
  }

  def completeAndConfirmResponseModelModel(id: String = TransactionIdValid,
                                           timestamp: DateTime = TransactionTimestampValid): Cookie = {
    val key = ChangeKeeperCompletionResponseCacheKey
    val value = CompleteAndConfirmResponseModel(id, timestamp)
    createCookie(key, value)
  }
}
