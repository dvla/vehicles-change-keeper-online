package composition

import com.google.inject.util.Modules
import com.google.inject.{Guice, Injector, Module}
import com.tzavellas.sse.guice.ScalaModule
import org.scalatest.mock.MockitoSugar
import play.api.Logger
import uk.gov.dvla.vehicles.presentation.common
import common.clientsidesession.CookieFlags
import common.clientsidesession.NoCookieFlags
import common.clientsidesession.ClientSideSessionFactory
import common.clientsidesession.ClearTextClientSideSessionFactory
import common.filters.{DateTimeZoneServiceImpl, DateTimeZoneService}
import common.services.DateService
import common.webserviceclients.addresslookup.{AddressLookupWebService, AddressLookupService}
import common.webserviceclients.addresslookup.ordnanceservey.AddressLookupServiceImpl
import common.webserviceclients.bruteforceprevention.BruteForcePreventionWebService
import common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupWebService
import utils.helpers.Config
import webserviceclients.fakes.brute_force_protection.FakeBruteForcePreventionWebServiceImpl
import webserviceclients.fakes.FakeAddressLookupWebServiceImpl
import webserviceclients.fakes.FakeDateServiceImpl
import webserviceclients.fakes.FakeVehicleAndKeeperLookupWebService

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
    bind[AddressLookupService].to[AddressLookupServiceImpl]

    val fakeWebServiceImpl = new FakeAddressLookupWebServiceImpl(
      responseOfPostcodeWebService = FakeAddressLookupWebServiceImpl.responseValidForPostcodeToAddress,
      responseOfUprnWebService = FakeAddressLookupWebServiceImpl.responseValidForUprnToAddress
    )
    bind[AddressLookupWebService].toInstance(fakeWebServiceImpl)
  }
}