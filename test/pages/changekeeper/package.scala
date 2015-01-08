package pages

import uk.gov.dvla.vehicles.presentation.common.ConfigProperties.getProperty

package object changekeeper {
  private final val applicationContext = getProperty[String]("application.context")

  def buildAppUrl(urlPart: String) = {
    val appContextWithSlash = if (!applicationContext.endsWith("/")) s"$applicationContext/" else applicationContext
    val urlPartWithoutSlash = urlPart.dropWhile(_ == '/')

    appContextWithSlash + urlPartWithoutSlash
  }
}
