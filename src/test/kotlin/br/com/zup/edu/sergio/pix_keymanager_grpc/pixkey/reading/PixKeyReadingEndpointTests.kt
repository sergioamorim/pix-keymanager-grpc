package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.assertIsFieldViolationWithADescription
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertStatus
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
class PixKeyReadingEndpointTests @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val grpcClient: PixKeyReadingServiceGrpc.PixKeyReadingServiceBlockingStub
) {

  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @Nested
  inner class HappyPath

  @Nested
  inner class RequestValidationTests {
    @Test
    fun `should return INVALID_ARGUMENT when the client_id is not an UUID`() {
      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest
            .newBuilder()
            .setClientId("not an UUID")
            .setPixId(UUID.randomUUID().toString())
            .build()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
        statusRuntimeException.assertIsFieldViolationWithADescription(field = "client_id")
      }
    }

    @Test
    fun `should return INVALID_ARGUMENT when the pix_id is not an UUID`() {
      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest
            .newBuilder()
            .setClientId(UUID.randomUUID().toString())
            .setPixId("not an UUID :P ")
            .build()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
        statusRuntimeException.assertIsFieldViolationWithADescription(field = "pix_id")
      }
    }

    @Test
    fun `should return INVALID_ARGUMENT when all fields are set`() {
      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest
            .newBuilder()
            .setClientId(UUID.randomUUID().toString())
            .setPixId(UUID.randomUUID().toString())
            .setPixKey(UUID.randomUUID().toString())
            .build()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
      }
    }

    @Test
    fun `should return INVALID_ARGUMENT when all fields are empty`() {
      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest.getDefaultInstance()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(status = Status.INVALID_ARGUMENT)
      }
    }

    @Test
    fun `should return PERMISSION_DENIED when the client doesn't own the pix key`() {
      val pixId: String = this@PixKeyReadingEndpointTests.pixKeyRepository.save(
        PixKey(
          type = PixKey.KeyType.RANDOM,
          key = UUID.randomUUID().toString(),
          clientId = UUID.randomUUID().toString(),
          accountType = PixKey.AccountType.CHECKING,
          participant = "60701190"
        )
      ).id ?: ""

      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest
            .newBuilder()
            .setPixId(pixId)
            .setClientId(UUID.randomUUID().toString())
            .build()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(Status.PERMISSION_DENIED)
        statusRuntimeException.assertIsFieldViolationWithADescription(field = "pix_id")
      }
    }

    @Test
    fun `should return NOT_FOUND when the pix id can't be found on the database`() {
      assertThrows<StatusRuntimeException> {
        this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
          PixKeyReadingOneRequest
            .newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClientId(UUID.randomUUID().toString())
            .build()
        )
      }.also { statusRuntimeException ->
        statusRuntimeException.assertStatus(Status.NOT_FOUND)
        statusRuntimeException.assertIsFieldViolationWithADescription(field = "pix_id")
      }
    }
  }
}
