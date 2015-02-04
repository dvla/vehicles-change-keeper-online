package composition

import com.google.inject.util.Modules
import com.google.inject.{Guice, Injector, Module}
import com.google.inject.name.Names
import com.tzavellas.sse.guice.ScalaModule
import org.scalatest.mock.MockitoSugar
import play.api.{LoggerLike, Logger}
import uk.gov.dvla.vehicles.presentation.common.filters.{DateTimeZoneServiceImpl, DateTimeZoneService}
import uk.gov.dvla.vehicles.presentation.common.services.DateService
import utils.helpers.Config
import webserviceclients.fakes.{FakeAddressLookupWebServiceImpl, FakeDateServiceImpl, FakeVehicleAndKeeperLookupWebService}
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieFlags
import common.clientsidesession.NoCookieFlags
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.ClearTextClientSideSessionFactory
import common.filters.AccessLoggingFilter.AccessLoggerName
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.bruteforceprevention.BruteForcePreventionServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionService
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupServiceImpl
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.{AddressLookupWebService, AddressLookupService}

trait TestComposition extends Composition {
  override lazy val injector: Injector = Guice.createInjector(testMod)

  private def testMod = Modules.`override`(new DevModule {
    override def bindSessionFactory() = ()
  }).`with`(new TestModule)

  def testModule(module: Module*) = Modules.`override`(testMod).`with`(module: _*)

  def testInjector(module: Module*) = Guice.createInjector(testModule(module: _*))
}

private class TestModule() extends ScalaModule with MockitoSugar {
  /**
   * Bind the fake implementations the traits
   */
  def configure() {
    Logger.debug("Guice is loading TestModule")
    bind[Config].to[TestConfig]

    ordnanceSurveyAddressLookup()
    bind[VehicleAndKeeperLookupWebService].to[FakeVehicleAndKeeperLookupWebService].asEagerSingleton()

    bind[DateService].to[FakeDateServiceImpl].asEagerSingleton()
    bind[DateTimeZoneService].toInstance(new DateTimeZoneServiceImpl)
    bind[CookieFlags].to[NoCookieFlags].asEagerSingleton()
    bind[ClientSideSessionFactory].to[ClearTextClientSideSessionFactory].asEagerSingleton()

    bind[BruteForcePreventionWebService].to[FakeBruteForcePreventionWebServiceImpl].asEagerSingleton()
  }

  private def ordnanceSurveyAddressLookup() = {
    bind[AddressLookupService].to[uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl]

    val fakeWebServiceImpl = new FakeAddressLookupWebServiceImpl(
      responseOfPostcodeWebService = FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress,
      responseOfUprnWebService = FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
    )
    bind[AddressLookupWebService].toInstance(fakeWebServiceImpl)
  }
}
