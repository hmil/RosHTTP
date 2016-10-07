package fr.hmil.roshttp

/** Low-level configuration for the HTTP client backend
  *
  * @param maxChunkSize Maximum size of each data chunk in streamed responses.
  */
class BackendConfig private(
    val maxChunkSize: Int
)

object BackendConfig {
  def apply(
     maxChunkSize: Int = 8192
   ): BackendConfig = new BackendConfig(
    maxChunkSize = maxChunkSize
  )
}