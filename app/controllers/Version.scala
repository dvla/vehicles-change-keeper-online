package controllers

import com.google.inject.Inject
import uk.gov.dvla.vehicles.presentation.common.controllers
import uk.gov.dvla.vehicles.presentation.common.controllers.Version.Suffix
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.acquire.AcquireConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.addresslookup.ordnanceservey.OrdnanceSurveyConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.emailservice.EmailServiceConfig
import uk.gov.dvla.vehicles.presentation.common.webserviceclients.vehicleandkeeperlookup.VehicleAndKeeperLookupConfig

class Version @Inject()(vehicleAndKeeperConfig: VehicleAndKeeperLookupConfig,
                        osAddressLookupConfig: OrdnanceSurveyConfig,
                        vehiclesAcquireConfig: AcquireConfig,
                        emailConfig: EmailServiceConfig)
  extends controllers.Version(
    emailConfig.emailServiceMicroServiceBaseUrl + Suffix,
    osAddressLookupConfig.baseUrl + Suffix,
    vehicleAndKeeperConfig.vehicleAndKeeperLookupMicroServiceBaseUrl + Suffix,
    vehiclesAcquireConfig.baseUrl + Suffix
  )
