package fr.hmil.roshttp

/** SSL configuration for the HTTP client backend
 *
 * @param keyStorePath Path to a p12/pfx file type keystore containing the key material.
 * @param keyStorePassphrase Password of the keystore file.
 * @param trustStorePaths Path to n-amount of pem files containing one or more certificates.
 */
class SSLConfig private(
    val keyStorePath: Option[String],
    val keyStorePassphrase: Option[Array[Char]],
    val trustStorePaths: Option[Array[String]]
)

object SSLConfig {
  def apply(
             keyStorePath: Option[String] = Option.empty,
             keyStorePassphrase: Option[Array[Char]] = Option.empty,
             trustStorePath: Option[Array[String]] = Option.empty
           ): SSLConfig = new SSLConfig(
    keyStorePath = keyStorePath,
    keyStorePassphrase = keyStorePassphrase,
    trustStorePaths = trustStorePath
  )
}
