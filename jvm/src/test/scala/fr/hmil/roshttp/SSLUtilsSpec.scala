package fr.hmil.roshttp

import javax.net.ssl.X509TrustManager
import utest.{TestSuite, test}

object SSLUtilsSpec extends TestSuite {

  private val KEYSTORE_P12_PATH = "jvm/src/test/resources/badssl.com-client.p12"
  private val KEYSTORE_PFX_PATH = "jvm/src/test/resources/badssl.com-client.pfx"
  private val KEYSTORE_PASSPHRASE = "badssl.com".toCharArray

  private val BADSSL_CERTIFICATE_PATH = "jvm/src/test/resources/badssl.com-certificate.pem"
  private val GITHUB_CERTIFICATE_PATH = "jvm/src/test/resources/github.pem"
  private val STACKEXCHANGE_CERTIFICATE_PATH = "jvm/src/test/resources/stackexchange.pem"
  private val MULTIPLE_CERTIFICATES_PATH = "jvm/src/test/resources/multiple-certificates.pem"

  val tests = this {

    test("Create key manager with key material") - {
      test("Create key manager with key material as p12 file") - {
        val keyManagers = SSLUtils.createKeyManagers(KEYSTORE_P12_PATH, KEYSTORE_PASSPHRASE)
        assert(keyManagers.length == 1)
      }

      test("Create key manager with key material as pfx file") - {
        val keyManagers = SSLUtils.createKeyManagers(KEYSTORE_PFX_PATH, KEYSTORE_PASSPHRASE)
        assert(keyManagers.length == 1)
      }
    }

    test("Create trust manager with trust material") - {
      test("Create trust manager with a single trusted certificate") - {
        val trustManagers = SSLUtils.createTrustManagers(Array(BADSSL_CERTIFICATE_PATH))
        assert(trustManagers.length == 1)
        assert(trustManagers(0).asInstanceOf[X509TrustManager].getAcceptedIssuers.length == 1)
      }

      test("Create trust manager with multiple trusted certificates as a single file") - {
        val trustManagers = SSLUtils.createTrustManagers(Array(MULTIPLE_CERTIFICATES_PATH))
        assert(trustManagers.length == 1)
        assert(trustManagers(0).asInstanceOf[X509TrustManager].getAcceptedIssuers.length == 3)
      }

      test("Create trust manager with multiple trusted certificates with multiple files") - {
        val trustManagers = SSLUtils.createTrustManagers(Array(
          BADSSL_CERTIFICATE_PATH,
          GITHUB_CERTIFICATE_PATH,
          STACKEXCHANGE_CERTIFICATE_PATH
        ))
        assert(trustManagers.length == 1)
        assert(trustManagers(0).asInstanceOf[X509TrustManager].getAcceptedIssuers.length == 3)
      }
    }

    test("Create SSLContext with key material") - {
      test("Create SSLContext with key material as p12 file") - {
        val sslContext = SSLUtils.createSslContext(
          SSLConfig.apply(
            keyStorePath = Option.apply(KEYSTORE_P12_PATH),
            keyStorePassphrase = Option.apply(KEYSTORE_PASSPHRASE)
          )
        )
        assert(sslContext != null)
      }

      test("Create SSLContext with key material as pfx file") - {
        val sslContext = SSLUtils.createSslContext(
          SSLConfig.apply(
            keyStorePath = Option.apply(KEYSTORE_PFX_PATH),
            keyStorePassphrase = Option.apply(KEYSTORE_PASSPHRASE)
          )
        )
        assert(sslContext != null)
      }
    }

    test("Create SSLContext with trust material") - {
      test("Create SSLContext with a single trusted certificate") - {
        val sslContext = SSLUtils.createSslContext(
          SSLConfig.apply(
            trustStorePath = Option.apply(Array(BADSSL_CERTIFICATE_PATH))
          )
        )
        assert(sslContext != null)
      }

      test("Create SSLContext with multiple trusted certificates as a single file") - {
        val sslContext = SSLUtils.createSslContext(
          SSLConfig.apply(
            trustStorePath = Option.apply(Array(MULTIPLE_CERTIFICATES_PATH))
          )
        )
        assert(sslContext != null)
      }

      test("Create SSLContext with multiple trusted certificates with multiple files") - {
        val sslContext = SSLUtils.createSslContext(
          SSLConfig.apply(
            trustStorePath = Option.apply(Array(
              BADSSL_CERTIFICATE_PATH,
              GITHUB_CERTIFICATE_PATH,
              STACKEXCHANGE_CERTIFICATE_PATH
            ))
          )
        )
        assert(sslContext != null)
      }
    }

    test("Create SSLContext with key and trust material") - {
      val sslContext = SSLUtils.createSslContext(
        SSLConfig.apply(
          keyStorePath = Option.apply(KEYSTORE_PFX_PATH),
          keyStorePassphrase = Option.apply(KEYSTORE_PASSPHRASE),
          trustStorePath = Option.apply(Array(BADSSL_CERTIFICATE_PATH))
        )
      )
      assert(sslContext != null)
    }
  }

}
