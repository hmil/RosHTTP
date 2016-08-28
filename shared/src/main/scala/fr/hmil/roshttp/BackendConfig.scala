package fr.hmil.roshttp

/** Low-level configuration for the HTTP client backend
  * @param maxChunkSize Maximum size of each data chunk in streamed responses.
  * @param bodyCollectTimeout Timeout for collecting the request body in a SimpleHttpResponse
  */
class BackendConfig private(
    val maxChunkSize: Int,
    val bodyCollectTimeout: Int
)

object BackendConfig {
  def apply(
     streamChunkSize: Int = 4096,
     bodyCollectTimeout: Int = 10
   ): BackendConfig = new BackendConfig(
    maxChunkSize = streamChunkSize,
    bodyCollectTimeout = bodyCollectTimeout
  )
}