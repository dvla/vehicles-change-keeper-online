package helpers

import composition.TestComposition
import pages.changekeeper.BusinessKeeperDetailsPage.{FleetNumberValid, BusinessNameValid}
import pages.changekeeper.NewKeeperChooseYourAddressPage
import play.api.libs.json.{Json, Writes}
import models.CompleteAndConfirmFormModel
import models.CompleteAndConfirmResponseModel
import models.CompleteAndConfirmResponseModel.ChangeKeeperCompletionResponseCacheKey
import models.DateOfSaleFormModel
import models.K2KCacheKeyPrefix.CookiePrefix
import models.SellerEmailModel
import models.SellerEmailModel.SellerEmailModelCacheKey
import models.VehicleLookupFormModel
import models.VehicleLookupFormModel.VehicleLookupFormModelCacheKey
import org.joda.time.{DateTime, LocalDate}
import pages.changekeeper.DateOfSalePage.DayDateOfSaleValid
import pages.changekeeper.DateOfSalePage.MileageValid
import pages.changekeeper.DateOfSalePage.MonthDateOfSaleValid
import pages.changekeeper.DateOfSalePage.YearDateOfSaleValid
import pages.changekeeper.PrivateKeeperDetailsPage.DayDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.DriverNumberValid
import pages.changekeeper.PrivateKeeperDetailsPage.EmailValid
import pages.changekeeper.PrivateKeeperDetailsPage.FirstNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.LastNameValid
import pages.changekeeper.PrivateKeeperDetailsPage.MonthDateOfBirthValid
import pages.changekeeper.PrivateKeeperDetailsPage.PostcodeValid
import pages.changekeeper.PrivateKeeperDetailsPage.YearDateOfBirthValid
import play.api.mvc.Cookie
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.{TrackingId, ClearTextClientSideSession, ClientSideSessionFactory, CookieFlags}
import common.mappings.TitleType
import common.model.AddressModel
import common.model.BruteForcePreventionModel
import common.model.BruteForcePreventionModel.bruteForcePreventionViewModelCacheKey
import common.model.BusinessKeeperDetailsFormModel
import common.model.BusinessKeeperDetailsFormModel.businessKeeperDetailsCacheKey
import common.model.MicroserviceResponseModel
import common.model.MicroserviceResponseModel.MsResponseCacheKey
import common.model.NewKeeperChooseYourAddressFormModel
import common.model.NewKeeperDetailsViewModel
import common.model.NewKeeperDetailsViewModel.newKeeperDetailsCacheKey
import common.model.NewKeeperEnterAddressManuallyFormModel
import common.model.NewKeeperEnterAddressManuallyFormModel.newKeeperEnterAddressManuallyCacheKey
import common.model.PrivateKeeperDetailsFormModel
import common.model.PrivateKeeperDetailsFormModel.privateKeeperDetailsCacheKey
import common.model.SeenCookieMessageCacheKey
import common.model.VehicleAndKeeperDetailsModel
import common.model.VehicleAndKeeperDetailsModel.vehicleAndKeeperLookupDetailsCacheKey
import common.views.models.{AddressLinesViewModel, AddressAndPostcodeViewModel}
import common.webserviceclients.common.MicroserviceResponse
import views.changekeeper.VehicleLookup.VehicleSoldTo_Private
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl.MaxAttempts
import webserviceclients.fakes.FakeAddressLookupService.BuildingNameOrNumberValid
import webserviceclients.fakes.FakeAddressLookupService.Line2Valid
import webserviceclients.fakes.FakeAddressLookupService.Line3Valid
import webserviceclients.fakes.FakeAddressLookupService.PostTownValid
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl.UprnValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.RegistrationNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.ReferenceNumberValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.{TransactionIdValid, TransactionTimestampValid}
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleMakeValid
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService.VehicleModelValid

object CookieFactoryForUnitSpecs extends TestComposition {

  implicit private val cookieFlags = injector.getInstance(classOf[CookieFlags])
  final val TrackingIdValue = "trackingId"
  final val KeeperEmail = "abc@def.com"
  final val SeenCookieTrue = "yes"
  final val ConsentTrue = "true"
  final val VehicleLookupFailureResponseMessage = "vehiclelookupfailure"
  private val session = new ClearTextClientSideSession(TrackingId(TrackingIdValue))

  private def createCookie[A](key: String, value: A)(implicit tjs: Writes[A]): Cookie = {
    val json = Json.toJson(value).toString()
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, json)
  }

  private def createCookie[A](key: String, value: String): Cookie = {
    val cookieName = session.nameCookie(key)
    session.newCookie(cookieName, value)
  }

  def withIdentifier(id: String) = {
    createCookie(models.IdentifierCacheKey, id)
  }

  def seenCookieMessage(): Cookie = {
    val key = SeenCookieMessageCacheKey
    val value = SeenCookieTrue
    createCookie(key, value)
  }

  def trackingIdModel(value: String = TrackingIdValue): Cookie = {
    createCookie(ClientSideSessionFactory.TrackingIdCookieName, value)
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
                                   address: Option[AddressModel] = None,
                                   keeperEndDate: Option[DateTime] = None,
                                   keeperChangeDate: Option[DateTime] = None): Cookie = {
    val key = vehicleAndKeeperLookupDetailsCacheKey
    val value = VehicleAndKeeperDetailsModel(
      registrationNumber = registrationNumber,
      make = vehicleMake,
      model = vehicleModel,
      title = title,
      firstName = firstName,
      lastName = lastName,
      address = address,
      keeperEndDate = keeperEndDate,
      keeperChangeDate = keeperChangeDate,
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
    val key = NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
    val value = NewKeeperChooseYourAddressFormModel(uprnSelected = uprnSelected)
    createCookie(key, value)
  }

  def newKeeperChooseYourAddress(uprnSelected: String = NewKeeperChooseYourAddressPage.selectedAddress): Cookie = {
    val key = NewKeeperChooseYourAddressFormModel.newKeeperChooseYourAddressCacheKey
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
    createCookie(key, value)
  }

  def newKeeperEnterAddressManually(): Cookie = {
    val key = newKeeperEnterAddressManuallyCacheKey
    val value = NewKeeperEnterAddressManuallyFormModel(
      addressAndPostcodeModel = AddressAndPostcodeViewModel(
        addressLinesModel = AddressLinesViewModel(
          buildingNameOrNumber = BuildingNameOrNumberValid,
          line2 = Some(Line2Valid),
          line3 = Some(Line3Valid),
          postTown = PostTownValid
        ),
      postCode = PostcodeValid
      )
    )
    createCookie(key, value)
  }

  def bruteForcePreventionViewModel(permitted: Boolean = true,
                                    attempts: Int = 0,
                                    maxAttempts: Int = MaxAttempts,
                                    dateTimeISOChronology: String = org.joda.time.DateTime.now().toString): Cookie = {
    val key = bruteForcePreventionViewModelCacheKey
    val value = BruteForcePreventionModel(
      permitted,
      attempts,
      maxAttempts,
      dateTimeISOChronology = dateTimeISOChronology
    )
    createCookie(key, value)
  }

  def vehicleLookupResponse(responseMessage: String = VehicleLookupFailureResponseMessage): Cookie =
    createCookie(MsResponseCacheKey, MicroserviceResponseModel(MicroserviceResponse("", responseMessage)))

  def allowGoingToCompleteAndConfirm(): Cookie =
    createCookie(CompleteAndConfirmFormModel.AllowGoingToCompleteAndConfirmPageCacheKey, "")

  def dateOfSaleModel(mileage: Option[Int] = Some(MileageValid.toInt),
                      dateOfSale: LocalDate = new LocalDate(
                        YearDateOfSaleValid.toInt,
                        MonthDateOfSaleValid.toInt,
                        DayDateOfSaleValid.toInt
                      )): Cookie =
    createCookie(DateOfSaleFormModel.DateOfSaleCacheKey, DateOfSaleFormModel(
      mileage,
      dateOfSale
    ))

  def completeAndConfirmModel(regRight: String = ConsentTrue, consent: String = ConsentTrue): Cookie = {
    val key = CompleteAndConfirmFormModel.CompleteAndConfirmCacheKey
    val value = CompleteAndConfirmFormModel(
      regRight,
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

  def sellerEmailModel(email: Option[String] = Some(KeeperEmail)): Cookie = {
    val key = SellerEmailModelCacheKey
    val value = SellerEmailModel(email)
    createCookie(key, value)
  }
}
