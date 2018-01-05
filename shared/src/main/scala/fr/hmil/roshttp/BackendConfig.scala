package fr.hmil.roshttp

/** Low-level configuration for the HTTP client backend
  *
  * @param maxChunkSize Maximum size of each data chunk in streamed requests/responses.
  * @param internalBufferLength Maximum number of chunks of response data to buffer when the network is faster than what
  *                             the stream consumer can handle.
  * @param allowChunkedRequestBody If set to false, HTTP chunked encoding will be disabled (i.e. the request payload
  *                                cannot be streamed).
  */
class BackendConfig private(
    val maxChunkSize: Int,
    val internalBufferLength: Int,
    val allowChunkedRequestBody: Boolean
)

object BackendConfig {
  def apply(
     maxChunkSize: Int = 8192,
     internalBufferLength: Int = 128,
     allowChunkedRequestBody: Boolean = true
   ): BackendConfig = new BackendConfig(
    maxChunkSize = maxChunkSize,
    internalBufferLength = internalBufferLength,
    allowChunkedRequestBody = allowChunkedRequestBody
  )
}
