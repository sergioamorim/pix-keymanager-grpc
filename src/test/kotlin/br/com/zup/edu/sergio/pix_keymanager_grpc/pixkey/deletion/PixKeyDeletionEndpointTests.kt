package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.deletion

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
  private val pixKeyRepository: PixKeyRepository
) {

  @BeforeEach
  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @Test
  fun `should delete an existing pix key when the client id matches`() {
    val clientId = UUID.randomUUID().toString()
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = "12345678901",
        clientId = clientId,
        accountType = PixKey.AccountType.CHECKING
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
  fun `should return not found when there is no key with the id informed on the database`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(UUID.randomUUID().toString())
          .setClientId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      assertEquals(Status.NOT_FOUND.code, statusRuntimeException.status.code)
    }
  }

  @Test
  fun `should return permission denied when the key exists but the client id doesn't match`() {
    val pixId: String = this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.CPF,
        key = "12345678901",
        clientId = UUID.randomUUID().toString(),
        accountType = PixKey.AccountType.CHECKING
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
      assertEquals(Status.PERMISSION_DENIED.code, statusRuntimeException.status.code)
    }

    assertTrue(this.pixKeyRepository.existsById(pixId))
  }

  @Test
  fun `should return invalid argument when the pix id is not sent`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setClientId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      assertEquals(Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code)
    }
  }

  @Test
  fun `should return invalid argument when the client id is not sent`() {
    assertThrows<StatusRuntimeException> {
      this.pixKeyDeletionBlockingStub.deletePixKey(
        PixKeyDeletionRequest
          .newBuilder()
          .setPixId(UUID.randomUUID().toString())
          .build()
      )
    }.also { statusRuntimeException ->
      assertEquals(Status.INVALID_ARGUMENT.code, statusRuntimeException.status.code)
    }
  }
}
