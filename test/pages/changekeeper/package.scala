package pages

package object changekeeper {
  final val applicationContext = ""

  def buildAppUrl(urlPart: String) = {
    val appContextWithSlash = if (!applicationContext.endsWith("/")) s"$applicationContext/" else applicationContext
    val urlPartWithoutSlash = urlPart.dropWhile(_ == '/')

    appContextWithSlash + urlPartWithoutSlash
  }
}
