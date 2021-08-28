package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

@MicronautTest(transactional = false)
class PixKeyCreatorTests @Inject constructor(
  private val pixKeyCreator: PixKeyCreator,
  private val pixKeyRepository: PixKeyRepository
) {

  @Test
  fun `should return aborted when the database throws a ConstraintViolationException`() {
    val email = "sergio@zup"

    this.pixKeyRepository.deleteAll()
    this.pixKeyRepository.save(
      PixKey(
        type = PixKey.KeyType.EMAIL,
        key = email,
        clientId = UUID.randomUUID().toString(),
        accountType = PixKey.AccountType.CHECKING,
        participant = "60701190"
      )
    )

    lateinit var statusCode: Status.Code

    this.pixKeyCreator.createPixKey(
      PixKeyCreationRequest
        .newBuilder()
        .setType(ProtobufKeyType.KEY_TYPE_EMAIL)
        .setKey(email)
        .setClientId(UUID.randomUUID().toString())
        .setAccountType(ProtobufAccountType.ACCOUNT_TYPE_CHECKING)
        .build()
    ).subscribe(
      {},
      { error: Throwable ->
        statusCode = (error as StatusRuntimeException).status.code
      }
    )

    assertEquals(Status.ABORTED.code, statusCode)
  }

}
