package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.MockBeanFactory
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertIsFieldViolationWithADescription
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertStatus
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

@MicronautTest(transactional = false)
class PixKeyCreationEndpointTests @Inject constructor(
  private val grpcClient: PixKeyCreationServiceGrpc.PixKeyCreationServiceBlockingStub,
  private val pixKeyRepository: PixKeyRepository,
  private val mockBeanFactory: MockBeanFactory
) {

  @BeforeEach
  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @Nested
  inner class HappyPathTests {
    @Test
    fun `should save a valid RANDOM key and return it's id`() {
      val clientId: String = UUID.randomUUID().toString()

      this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setClientId(clientId)
          .setType(ProtobufKeyType.KEY_TYPE_RANDOM)
          .build()
      ).also { response: PixKeyCreationResponse ->
        assertTrue(
          this@PixKeyCreationEndpointTests.pixKeyRepository
            .existsById(response.pixId)
        )

        val pixKey: PixKey = this@PixKeyCreationEndpointTests.pixKeyRepository
          .getById(id = response.pixId)

        assertEquals(PixKey.AccountType.CHECKING, pixKey.accountType)
        assertEquals(clientId, pixKey.clientId)
        assertEquals(PixKey.KeyType.RANDOM, pixKey.type)
      }
    }

    @Test
    fun `should save a valid CPF key and return it's id`() {
      val key = "12345678901"

      val response: PixKeyCreationResponse =
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setClientId(UUID.randomUUID().toString())
            .setKey(key)
            .setType(ProtobufKeyType.KEY_TYPE_CPF)
            .build()
        )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository
          .existsById(response.pixId)
      )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should save a valid PHONE_NUMBER key and return it's id`() {
      val key = "+5585988714077"

      val response: PixKeyCreationResponse =
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
            .setClientId(UUID.randomUUID().toString())
            .setKey(key)
            .setType(ProtobufKeyType.KEY_TYPE_PHONE)
            .build()
        )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository
          .existsById(response.pixId)
      )
      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should save a valid EMAIL key and return it's id`() {
      val key = "sergio@zup.com"

      val response: PixKeyCreationResponse =
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setType(ProtobufKeyType.KEY_TYPE_EMAIL)
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setClientId(UUID.randomUUID().toString())
            .setKey(key)
            .build()
        )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository
          .existsById(response.pixId)
      )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should save a key with exactly 77 characters and return it's id`() {
      val key = "not.too.long.email@example.com".padStart(length = 77, padChar = 'a')

      val response: PixKeyCreationResponse =
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
            .setClientId(UUID.randomUUID().toString())
            .setKey(key)
            .setType(ProtobufKeyType.KEY_TYPE_EMAIL)
            .build()
        )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository
          .existsById(response.pixId)
      )

      assertTrue(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }
  }

  @Test
  fun `should return INVALID_ARGUMENT when the type is RANDOM and a key is sent`() {
    val key = UUID.randomUUID().toString()

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(ProtobufKeyType.KEY_TYPE_RANDOM)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
    }

    assertFalse(this.pixKeyRepository.existsByKey(key = key))
  }

  @Test
  fun `should return INVALID_ARGUMENT when a CPF key has an invalid CPF format`() {
    val key = "+5585988714077"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(ProtobufKeyType.KEY_TYPE_CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
      statusRuntimeException.assertIsFieldViolationWithADescription(field = "key")
    }

    assertFalse(this.pixKeyRepository.existsByKey(key = key))
  }


  @Test
  fun `should return NOT_FOUND when the account existence can't be confirmed`() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setClientId(this.mockBeanFactory.erpReadReturnsNotFoundClientId)
          .setType(ProtobufKeyType.KEY_TYPE_CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.NOT_FOUND)
    }

    assertFalse(this.pixKeyRepository.existsByKey(key = key))
  }

  @Test
  fun `should return UNAVAILABLE when the erp system returns an unknown status`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
          .setClientId(this.mockBeanFactory.erpReadReturnsUnknownResponseClientId)
          .setType(ProtobufKeyType.KEY_TYPE_RANDOM)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }
  }

  @Test
  fun `should return ALREADY_EXISTS when the bcb system returns unprocessable entity`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnprocessableEntityPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.ALREADY_EXISTS)
    }

    assertFalse(
      this.pixKeyRepository.existsByKey(
        key = this.mockBeanFactory.bcbCreateReturnsUnprocessableEntityPixKey
      )
    )
  }

  @Test
  fun `should return UNAVAILABLE when the bcb system returns an unknown response`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnknownResponsePixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }

    assertFalse(
      this.pixKeyRepository.existsByKey(
        key = this.mockBeanFactory.bcbCreateReturnsUnknownResponsePixKey
      )
    )
  }

  @Test
  fun `should return UNAVAILABLE when an HttpClientException is thrown when connecting to the bcb system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsHttpClientExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }

    assertFalse(
      this.pixKeyRepository.existsByKey(
        key = this.mockBeanFactory.bcbCreateReturnsHttpClientExceptionPixKey
      )
    )
  }

  @Test
  fun `should return INTERNAL when an unknown exception is thrown when connecting to the bcb system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnknownExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INTERNAL)
    }

    assertFalse(
      this.pixKeyRepository.existsByKey(
        key = this.mockBeanFactory.bcbCreateReturnsUnknownExceptionPixKey
      )
    )
  }

  @Test
  fun `should return UNAVAILABLE when an HttpClientException is thrown when connecting to the erp system`() {
    val key = "+55667788990"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(this.mockBeanFactory.erpReadReturnsHttpClientExceptionClientId)
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }

    this.pixKeyRepository.existsByKey(key = key)
  }

  @Test
  fun `should return INTERNAL when an unknown exception is thrown when connecting to the erp system`() {
    val key = "+55667788990"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(ProtobufKeyType.KEY_TYPE_PHONE)
          .setClientId(this.mockBeanFactory.erpReadReturnsUnknownExceptionClientId)
          .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INTERNAL)
    }

    assertFalse(this.pixKeyRepository.existsByKey(key = key))
  }

  @Nested
  inner class PixKeyCreationRequestValidationTests {
    @Test
    fun `should return INVALID_ARGUMENT when the accountType is not sent`() {
      val key = "+55667788990"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_PHONE)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "account_type")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when a PHONE_NUMBER key does not have a phone number format`() {
      val key = "sergio@zup.com.br"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_PHONE)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when the clientId is not an UUID`() {
      val key = "+55667788990"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setType(ProtobufKeyType.KEY_TYPE_PHONE)
            .setClientId("random string")
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "client_id")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when the type is not sent`() {
      val key = "+55667788990"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setClientId(UUID.randomUUID().toString())
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "type")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when the key has more than 77 characters`() {
      val key = "really.long.email@example.com".padStart(length = 78, padChar = 'a')

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_EMAIL)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when a CPF key request is sent without a key`() {
      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_CPF)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }
    }

    @Test
    fun `should return ALREADY_EXISTS when the key is already in the database`() {
      val key = "12345678901"
      this@PixKeyCreationEndpointTests.pixKeyRepository.save(
        PixKey(
          type = PixKey.KeyType.CPF,
          key = key,
          clientId = "asd",
          accountType = PixKey.AccountType.SAVINGS,
          participant = "60701190"
        )
      )

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_CPF)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.ALREADY_EXISTS)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }
    }

    @Test
    fun `should return INVALID_ARGUMENT when an EMAIL key has an invalid email format`() {
      val key = "12345678901"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_SAVINGS)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_EMAIL)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }

    @Test
    fun `should return INVALID_ARGUMENT when a PHONE_NUMBER key has an invalid phone number format`() {
      val key = "12345678901"

      assertThrows(StatusRuntimeException::class.java) {
        this@PixKeyCreationEndpointTests.grpcClient.createPixKey(
          PixKeyCreationRequest
            .newBuilder()
            .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
            .setClientId(UUID.randomUUID().toString())
            .setType(ProtobufKeyType.KEY_TYPE_PHONE)
            .setKey(key)
            .build()
        )
      }.also { statusRuntimeException: StatusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

        statusRuntimeException
          .assertIsFieldViolationWithADescription(field = "key")
      }

      assertFalse(
        this@PixKeyCreationEndpointTests.pixKeyRepository.existsByKey(key = key)
      )
    }
  }
}
