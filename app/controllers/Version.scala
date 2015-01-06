package controllers

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.controllers
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.VehicleAndKeeperLookupConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.config.OrdnanceSurveyConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.AcquireConfig

class Version @Inject()(vehiclesKeeperConfig: VehicleAndKeeperLookupConfig,
                        osAddressLookupConfig: OrdnanceSurveyConfig,
                        acquireConfig: AcquireConfig)
  extends controllers.Version(
    vehiclesKeeperConfig.vehicleAndKeeperLookupMicroServiceBaseUrl + controllers.Version.Suffix,
    osAddressLookupConfig.baseUrl + controllers.Version.Suffix,
    acquireConfig.baseUrl + controllers.Version.Suffix
  )
