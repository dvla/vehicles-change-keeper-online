package controllers

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.controllers
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.AcquireConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.OrdnanceSurveyConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupConfig

class Version @Inject()(vehiclesKeeperConfig: VehicleAndKeeperLookupConfig,
                        osAddressLookupConfig: OrdnanceSurveyConfig,
                        acquireConfig: AcquireConfig)
  extends controllers.Version(
    vehiclesKeeperConfig.vehicleAndKeeperLookupMicroServiceBaseUrl + controllers.Version.Suffix,
    osAddressLookupConfig.baseUrl + controllers.Version.Suffix,
    acquireConfig.baseUrl + controllers.Version.Suffix
  )
