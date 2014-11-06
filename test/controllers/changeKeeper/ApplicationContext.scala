package controllers.changeKeeper

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

object ApplicationContext {
  val applicationContext = getProperty("application.context", default = "")
}