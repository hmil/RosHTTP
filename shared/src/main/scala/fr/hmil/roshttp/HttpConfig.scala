package fr.hmil.roshttp

class HttpConfig private(
    /** Maximum size of each data chunk in a Streamed response.
     *
     * TODO: This is only enforced on JVM. Enforce on other envs as well
     */
    val streamChunkSize: Int,

    /** Timeout for collecting the request body in a SimpleHttpResponse */
    val bodyCollectTimeout: Int
)

object HttpConfig {

  def apply(
     streamChunkSize: Int = 4096,
     bodyCollectTimeout: Int = 10
   ): HttpConfig = new HttpConfig(
    streamChunkSize = streamChunkSize,
    bodyCollectTimeout = bodyCollectTimeout
  )

  implicit val default = HttpConfig()
}