package br.com.zup.edu.sergio.pix_keymanager_grpc

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ExternalAccountType
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import javax.inject.Inject
import org.mockito.Mockito.`when` as mockitoWhen

@MicronautTest(transactional = false)
class PixKeyServiceTest @Inject constructor(
  private val grpcClient: PixKeyServiceGrpc.PixKeyServiceBlockingStub,
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
        clientId = "invalid client id", accountType = ExternalAccountType.CONTA_POUPANCA
      )
    ).thenThrow(HttpClientResponseException("not found", HttpResponse.notFound<Any>()))

    return erpClient
  }

  @Test
  @DisplayName("Should save the pix key to the database and return it's id")
  fun shouldSaveThePixKeyToTheDatabaseAndReturnItSId() {
    val response: PixKeyResponse = this.grpcClient.createPixKey(
      PixKeyRequest
        .newBuilder()
        .setAccountType(AccountType.CHECKING)
        .setClientId("ae93a61c-0642-43b3-bb8e-a17072295955")
        .setType(PixKeyType.RANDOM)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
  }

  @Test
  @DisplayName("Should return illegal argument when the type is random and a key is sent")
  fun shouldReturnIllegalArgumentWhenTheTypeIsRandomAndAKeyIsSent() {
    val key = "something"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.CHECKING)
          .setClientId("client id")
          .setType(PixKeyType.RANDOM)
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
  @DisplayName("Should return illegal argument when a CPF key has an invalid CPF format")
  fun shouldReturnIllegalArgumentWhenACpfKeyHasAnInvalidCpfFormat() {
    val key = "+5585988714077"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.CHECKING)
          .setClientId("client id")
          .setType(PixKeyType.CPF)
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
  @DisplayName("Should return illegal argument when a phone number key has an invalid phone number format")
  fun shouldReturnIllegalArgumentWhenAPhoneNumberKeyHasAnInvalidPhoneNumberFormat() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.CHECKING)
          .setClientId("client id")
          .setType(PixKeyType.PHONE_NUMBER)
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
  @DisplayName("Should return illegal argument when a email key has an invalid email format")
  fun shouldReturnIllegalArgumentWhenAEmailKeyHasAnInvalidEmailFormat() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.SAVINGS)
          .setClientId("client id")
          .setType(PixKeyType.EMAIL)
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
  @DisplayName("Should return already exists when the key is already in the database")
  fun shouldReturnAlreadyExistsWhenTheKeyIsAlreadyInTheDatabase() {
    val key = "12345678901"
    this.pixKeyRepository.save(
      PixKey(
        type = PixKeyType.CPF,
        key = key,
        clientId = "asd",
        accountType = AccountType.SAVINGS
      )
    )

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.CHECKING)
          .setClientId("client id")
          .setType(PixKeyType.CPF)
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
  @DisplayName("Should save a valid CPF key and return it's id")
  fun shouldSaveAValidCpfKeyAndReturnItSId() {
    val key = "12345678901"

    val response: PixKeyResponse = this.grpcClient.createPixKey(
      PixKeyRequest
        .newBuilder()
        .setAccountType(AccountType.CHECKING)
        .setClientId("client id")
        .setKey(key)
        .setType(PixKeyType.CPF)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  @DisplayName("Should save a valid email key and return it's id")
  fun shouldSaveAValidEmailKeyAndReturnItSId() {
    val key = "sergio@zup.com"

    val response: PixKeyResponse = this.grpcClient.createPixKey(
      PixKeyRequest
        .newBuilder()
        .setType(PixKeyType.EMAIL)
        .setAccountType(AccountType.CHECKING)
        .setClientId("client id")
        .setKey(key)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  @DisplayName("Should save a valid phone number key and return it's id")
  fun shouldSaveAValidPhoneNumberKeyAndReturnItSId() {
    val key = "+5585988714077"

    val response: PixKeyResponse = this.grpcClient.createPixKey(
      PixKeyRequest
        .newBuilder()
        .setAccountType(AccountType.SAVINGS)
        .setClientId("client id")
        .setKey(key)
        .setType(PixKeyType.PHONE_NUMBER)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  @DisplayName("Should return illegal argument when a CPF key request is sent without a key")
  fun shouldReturnIllegalArgumentWhenACpfKeyRequestIsSentWithoutAKey() {
    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.SAVINGS)
          .setClientId("client id")
          .setType(PixKeyType.CPF)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code
      )
    }
  }

  @Test
  @DisplayName("Should return not found when the account existence can't be confirmed")
  fun shouldReturnNotFoundWhenTheAccountExistenceCanTBeConfirmed() {
    val key = "12345678901"

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.CHECKING)
          .setClientId("invalid client id")
          .setType(PixKeyType.CPF)
          .setKey(key)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      assertEquals(
        Status.NOT_FOUND.code, statusRuntimeException.status.code
      )
    }

    assertFalse(this.pixKeyRepository.existsByKey(key))
  }

  @Test
  @DisplayName("Should return illegal argument when the key has more than 77 characters")
  fun shouldReturnIllegalArgumentWhenTheKeyHasMoreThan77Characters() {
    val key = "really.long.email@example.com".padStart(length = 78, padChar = 'a')

    assertThrows(StatusRuntimeException::class.java) {
      this.grpcClient.createPixKey(
        PixKeyRequest
          .newBuilder()
          .setAccountType(AccountType.SAVINGS)
          .setClientId("client id")
          .setType(PixKeyType.EMAIL)
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
  @DisplayName("Should save a key with exactly 77 characters and return it's id")
  fun shouldSaveAKeyWithExactly77CharactersAndReturnItSId() {
    val key = "not.too.long.email@example.com".padStart(length = 77, padChar = 'a')

    val response: PixKeyResponse = this.grpcClient.createPixKey(
      PixKeyRequest
        .newBuilder()
        .setAccountType(AccountType.SAVINGS)
        .setClientId("client id")
        .setKey(key)
        .setType(PixKeyType.EMAIL)
        .build()
    )

    assertTrue(this.pixKeyRepository.existsById(response.pixId))
    assertTrue(this.pixKeyRepository.existsByKey(key))
  }

}
