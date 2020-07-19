package fr.hmil.roshttp

import java.io._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.security.cert.{CertificateFactory, X509Certificate}
import java.security.{KeyStore, SecureRandom}
import java.util.Objects.nonNull
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.{Base64, Objects}

import javax.net.ssl._

object SSLUtils {

  private val SSLCONTEXT_TYPE = "TLSv1.2"
  private val KEYSTORE_TYPE = "PKCS12"
  private val CERTIFICATE_TYPE = "X.509"
  private val CERTIFICATE_HEADER = "-----BEGIN CERTIFICATE-----"
  private val CERTIFICATE_FOOTER = "-----END CERTIFICATE-----"
  private val CERTIFICATE_PATTERN = Pattern.compile(CERTIFICATE_HEADER + "(.*?)" + CERTIFICATE_FOOTER, Pattern.DOTALL)

  private val NEW_LINE = "\n"
  private val EMPTY = "";

  def createSslContext(sslConfig: SSLConfig): SSLContext = {
    val sslContext: SSLContext = SSLContext.getInstance(SSLCONTEXT_TYPE)
    var keyManagers: Array[KeyManager] = null
    var trustManagers: Array[TrustManager] = null

    if (sslConfig.keyStorePath.isDefined && sslConfig.keyStorePassphrase.isDefined) {
      keyManagers = createKeyManagers(sslConfig.keyStorePath.get, sslConfig.keyStorePassphrase.get)
    }

    if (sslConfig.trustStorePaths.isDefined) {
      trustManagers = createTrustManagers(sslConfig.trustStorePaths.get)
    }

    sslContext.init(keyManagers, trustManagers, new SecureRandom())
    sslContext
  }

  def createKeyManagers(keyStorePath: String, keyStorePassphrase: Array[Char]): Array[KeyManager] = {
      val keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
      val keyStoreInputStream = Files.newInputStream(Paths.get(keyStorePath))

      Objects.requireNonNull(keyStoreInputStream)
      keyStore.load(keyStoreInputStream, keyStorePassphrase)

      val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      keyManagerFactory.init(keyStore, keyStorePassphrase)

      closeSafely(keyStoreInputStream)
      keyManagerFactory.getKeyManagers
  }

  def createTrustManagers(trustStorePaths: Array[String]): Array[TrustManager] = {
    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType)
    trustStore.load(null)

    for (trustStorePath <- trustStorePaths) {
      val trustStoreStream: InputStream = Files.newInputStream(Paths.get(trustStorePath))
      Objects.requireNonNull(trustStoreStream)

      val inputStreamReader = new InputStreamReader(trustStoreStream, StandardCharsets.UTF_8)
      val bufferedReader = new BufferedReader(inputStreamReader)
      val content = bufferedReader.lines().collect(Collectors.joining(NEW_LINE))

      val matcher = CERTIFICATE_PATTERN.matcher(content)

      while (matcher.find()) {
        val certificateAsBytes = Base64.getDecoder.decode(matcher.group(1).replaceAll(NEW_LINE, EMPTY).trim)
        val certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE)

        val byteArrayInputStream = new ByteArrayInputStream(certificateAsBytes)
        val certificate = certificateFactory.generateCertificate(byteArrayInputStream).asInstanceOf[X509Certificate]

        trustStore.setCertificateEntry(certificate.getSubjectDN.getName, certificate)
        closeSafely(byteArrayInputStream)
      }

      closeSafely(bufferedReader)
      closeSafely(inputStreamReader)
      closeSafely(trustStoreStream)
    }

    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    trustManagerFactory.init(trustStore)
    trustManagerFactory.getTrustManagers
  }


  private def closeSafely(closeable: Closeable): Unit = {
    if (nonNull(closeable)) closeable.close()
  }

}
