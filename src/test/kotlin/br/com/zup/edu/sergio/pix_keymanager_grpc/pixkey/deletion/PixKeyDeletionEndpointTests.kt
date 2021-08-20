package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

import br.com.zup.edu.sergio.pix_keymanager_grpc.MockBeanFactory
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertIsFieldViolationWithADescription
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertStatus
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyDeletionServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
class PixKeyDeletionEndpointTests @Inject constructor(
  private val pixKeyDeletionBlockingStub: PixKeyDeletionServiceGrpc.PixKeyDeletionServiceBlockingStub,
  private val pixKeyRepository: PixKeyRepository,
  private val mockBeanFactory: MockBeanFactory
) {

  @BeforeEach
  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @Test
  fun `should delete an existing pix key when the clientId matches`() {
    val clientId: String = UUID.randomUUID().toString()
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = "12345678901",
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    this.pixKeyDeletionBlockingStub.deletePixKey(
      PixKeyDeletionRequest
        .newBuilder()
        .setPixId(pixId)
        .setClientId(clientId)
        .build()
    )

    assertFalse(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return NOT_FOUND when there is no pix key with the id informed on the database`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(UUID.randomUUID().toString())
          .setClientId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.NOT_FOUND)

      statusRuntimeException
        .assertIsFieldViolationWithADescription(field = "pixId")
    }
  }

  @Test
  fun `should return PERMISSION_DENIED when the key exists but the clientId doesn't match`() {
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = "12345678901",
        clientId = UUID.randomUUID().toString(),
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(pixId)
          .setClientId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.PERMISSION_DENIED)

      statusRuntimeException
        .assertIsFieldViolationWithADescription(field = "pixId")
    }

    assertTrue(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return INVALID_ARGUMENT when the pixId is not sent`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setClientId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)

      statusRuntimeException
        .assertIsFieldViolationWithADescription(field = "pixId")
    }
  }

  @Test
  fun `should return INVALID_ARGUMENT when the clientId is not sent`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
      statusRuntimeException.assertIsFieldViolationWithADescription(field = "clientId")
    }
  }

  @Test
  fun `should return INVALID_ARGUMENT when the clientId is not an UUID`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(UUID.randomUUID().toString())
          .setClientId("not")
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
      statusRuntimeException.assertIsFieldViolationWithADescription(field = "clientId")
    }
  }

  @Test
  fun `should delete the pix key id when it is not found within the bcb system`() {
    val clientId: String = UUID.randomUUID().toString()

    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = this.mockBeanFactory.bcbDeleteReturnsNotFoundPixKey,
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    this.pixKeyDeletionBlockingStub.deletePixKey(
      PixKeyDeletionRequest
        .newBuilder()
        .setPixId(pixId)
        .setClientId(clientId)
        .build()
    )

    assertFalse(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return UNAVAILABLE and not delete the pix key when the bcb server returns an unknown response`() {
    val clientId: String = UUID.randomUUID().toString()
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = this.mockBeanFactory.bcbDeleteReturnsUnknownResponsePixKey,
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    assertThrows(StatusRuntimeException::class.java) {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(pixId)
          .setClientId(clientId)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }

    assertTrue(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return UNAVAILABLE and not delete the pix key when an HttpClientException is thrown when connecting to the bcb system`() {
    val clientId: String = UUID.randomUUID().toString()
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = this.mockBeanFactory.bcbDeleteReturnsHttpClientExceptionPixKey,
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    assertThrows(StatusRuntimeException::class.java) {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(pixId)
          .setClientId(clientId)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.UNAVAILABLE)
    }

    assertTrue(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return INTERNAL and not delete the pix key when an unknown exception is thrown when connecting to the bcb system`() {
    val clientId: String = UUID.randomUUID().toString()
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = this.mockBeanFactory.bcbDeleteReturnsUnknownExceptionPixKey,
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    ).id ?: "should fail"

    assertThrows(StatusRuntimeException::class.java) {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(pixId)
          .setClientId(clientId)
          .build()
      )
    }.also { statusRuntimeException: StatusRuntimeException ->
      statusRuntimeException.assertStatus(status = Status.INTERNAL)
    }

    assertTrue(this.pixKeyRepository.existsById(pixId))
  }
}
