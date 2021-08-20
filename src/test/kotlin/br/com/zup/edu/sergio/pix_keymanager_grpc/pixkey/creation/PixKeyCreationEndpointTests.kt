package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.MockBeanFactory
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.AccountType.CHECKING
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.AccountType.SAVINGS
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.KeyType.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject

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

  @Test
  fun `should save a valid random pix key to the database and return it's id`() {
    val clientId: String = UUID.randomUUID().toString()
    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setAccountType(CHECKING)
        .setClientId(clientId)
        .setType(RANDOM)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))

    this.pixKeyRepository.getById(response.pixId).also { pixKey: PixKey ->
      assertEquals(PixKey.AccountType.CHECKING, pixKey.accountType)
      assertEquals(clientId, pixKey.clientId)
      assertEquals(PixKey.KeyType.RANDOM, pixKey.type)
    }
  }

  @Test
  fun `should return illegal argument when the type is random and a key is sent`() {
    val key = "something"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(RANDOM)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return illegal argument when a CPF key has an invalid CPF format`() {
    val key = "+5585988714077"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return illegal argument when a phone number key has an invalid phone number format`() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId("client id")
          .setType(PHONE_NUMBER)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return illegal argument when an email key has an invalid email format`() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(SAVINGS)
          .setClientId(UUID.randomUUID().toString())
          .setType(EMAIL)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return already exists when the key is already in the database`() {
    val key = "12345678901"
    this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = key,
        clientId = "asd",
        accountType = PixKey.AccountType.SAVINGS,
        participant = "60701190"
      )
    )

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.ALREADY_EXISTS.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should save a valid CPF key and return it's id`() {
    val key = "12345678901"

    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setAccountType(CHECKING)
        .setClientId(UUID.randomUUID().toString())
        .setKey(key)
        .setType(CPF)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should save a valid email key and return it's id`() {
    val key = "sergio@zup.com"

    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setType(EMAIL)
        .setAccountType(CHECKING)
        .setClientId(UUID.randomUUID().toString())
        .setKey(key)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should save a valid phone number key and return it's id`() {
    val key = "+5585988714077"

    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setAccountType(SAVINGS)
        .setClientId(UUID.randomUUID().toString())
        .setKey(key)
        .setType(PHONE_NUMBER)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return illegal argument when a CPF key request is sent without a key`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(SAVINGS)
          .setClientId(UUID.randomUUID().toString())
          .setType(CPF)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return not found when the account existence can't be confirmed`() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId(this.mockBeanFactory.erpReadReturnsNotFoundClientId)
          .setType(CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(Status.NOT_FOUND.code, statusRuntimeException.status.code)
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return illegal argument when the key has more than 77 characters`() {
    val key = "really.long.email@example.com".padStart(length = 78, padChar = 'a')

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(SAVINGS)
          .setClientId(UUID.randomUUID().toString())
          .setType(EMAIL)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should save a key with exactly 77 characters and return it's id`() {
    val key = "not.too.long.email@example.com".padStart(length = 77, padChar = 'a')

    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setAccountType(SAVINGS)
        .setClientId(UUID.randomUUID().toString())
        .setKey(key)
        .setType(EMAIL)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  fun `should return unavailable when the erp system returns an unknown status`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(SAVINGS)
          .setClientId(this.mockBeanFactory.erpReadReturnsUnknownResponseClientId)
          .setType(RANDOM)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(Status.UNAVAILABLE.code, statusRuntimeException.status.code)
    }
  }

  @Test
  fun `should return invalid argument when the account type is not sent`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setClientId(UUID.randomUUID().toString())
          .setType(PHONE_NUMBER)
          .setKey("+55667788990")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return invalid argument when the type is not sent`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(CHECKING)
          .setKey("+55667788990")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return invalid argument when the client id is not an UUID`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId("random string")
          .setAccountType(CHECKING)
          .setKey("+55667788990")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return already exists when the bcb system returns unprocessable entity`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnprocessableEntityPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.ALREADY_EXISTS.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return unavailable when the bcb system returns an unknown response`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnknownResponsePixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.UNAVAILABLE.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return unavailable when an HttpClientException is thrown when connecting to the bcb system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsHttpClientExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.UNAVAILABLE.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return internal error when an unknown exception is thrown when connecting to the bcb system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(UUID.randomUUID().toString())
          .setAccountType(CHECKING)
          .setKey(this.mockBeanFactory.bcbCreateReturnsUnknownExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INTERNAL.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return unavailable when an HttpClientException is thrown when connecting to the erp system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(this.mockBeanFactory.erpReadReturnsHttpClientExceptionClientId)
          .setAccountType(CHECKING)
          .setKey("+55667788990")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.UNAVAILABLE.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return internal error when an unknown exception is thrown when connecting to the erp system`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PHONE_NUMBER)
          .setClientId(this.mockBeanFactory.erpReadReturnsUnknownExceptionClientId)
          .setAccountType(CHECKING)
          .setKey("+55667788990")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INTERNAL.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  fun `should return invalid argument when a phone number key does not have a phone number format`() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId(UUID.randomUUID().toString())
          .setType(PHONE_NUMBER)
          .setKey("sergio@zup.com.br")
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }
}
