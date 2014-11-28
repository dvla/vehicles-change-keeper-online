package webserviceclients.acquire

import scala.concurrent.Future

trait AcquireService {
  def invoke(cmd: AcquireRequestDto, trackingId: String): Future[(Int, Option[AcquireResponseDto])]
}
