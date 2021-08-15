package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.AccountType.CHECKING
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.AccountType.SAVINGS
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest.KeyType.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationServiceGrpc
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ExternalAccountType
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import javax.inject.Inject
import org.mockito.Mockito.`when` as mockitoWhen

@MicronautTest(transactional = false)
class PixKeyCreationEndpointTests @Inject constructor(
  private val grpcClient: PixKeyCreationServiceGrpc.PixKeyCreationServiceBlockingStub,
  private val pixKeyRepository: PixKeyRepository
) {

  @BeforeEach
  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @MockBean(ErpClient::class)
  fun erpClientMock(): ErpClient {
    val erpClient = mock(ErpClient::class.java)

    mockitoWhen(
      erpClient.readAccount(
        clientId = "invalid client id", accountType = ExternalAccountType.CONTA_CORRENTE
      )
    ).thenThrow(HttpClientResponseException("not found", HttpResponse.notFound<Any>()))

    mockitoWhen(
      erpClient.readAccount(
        clientId = "force unavailable", accountType = ExternalAccountType.CONTA_POUPANCA
      )
    ).thenThrow(
      HttpClientResponseException(
        "unavailable", HttpResponse.status<Any>(HttpStatus.SERVICE_UNAVAILABLE)
      )
    )

    return erpClient
  }

  @Test
  fun `should save a valid random pix key to the database and return it's id`() {
    val response: PixKeyCreationResponse = this.grpcClient.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setAccountType(CHECKING)
        .setClientId("ae93a61c-0642-43b3-bb8e-a17072295955")
        .setType(RANDOM)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
  }

  @Test
  fun `should return illegal argument when the type is random and a key is sent`() {
    val key = "something"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId("client id")
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
          .setClientId("client id")
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
          .setClientId("client id")
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
        accountType = PixKey.AccountType.SAVINGS
      )
    )

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyCreationRequest
          .newBuilder()
          .setAccountType(CHECKING)
          .setClientId("client id")
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
        .setClientId("client id")
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
        .setClientId("client id")
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
        .setClientId("client id")
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
          .setClientId("client id")
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
          .setClientId("invalid client id")
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
          .setClientId("client id")
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
        .setClientId("client id")
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
          .setClientId("force unavailable")
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
          .setClientId("client id")
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
          .setClientId("client id")
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
}
