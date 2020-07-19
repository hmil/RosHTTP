package fr.hmil.roshttp

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
