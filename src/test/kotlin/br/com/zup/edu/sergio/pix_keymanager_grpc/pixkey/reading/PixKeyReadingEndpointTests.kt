package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.MockBeanFactory
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertIsFieldViolationWithADescription
import br.com.zup.edu.sergio.pix_keymanager_grpc.assertStatus
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKeyRepository
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
class PixKeyReadingEndpointTests @Inject constructor(
  private val pixKeyRepository: PixKeyRepository,
  private val grpcClient: PixKeyReadingServiceGrpc.PixKeyReadingServiceBlockingStub,
  private val mockBeanFactory: MockBeanFactory
) {

  fun setUp() {
    this.pixKeyRepository.deleteAll()
  }

  @Nested
  inner class HappyPath {
    @Test
    fun `should return details of the pix key when requesting by pix_id and client_id`() {
      val key: String = UUID.randomUUID().toString()
      val clientId: String = UUID.randomUUID().toString()
      val pixId: String = this@PixKeyReadingEndpointTests.pixKeyRepository.save(
        PixKey(
          type = PixKey.KeyType.RANDOM,
          key = key,
          clientId = clientId,
          accountType = PixKey.AccountType.CHECKING,
          participant = "60701190"
        )
      ).id ?: ""

      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId(pixId)
          .setClientId(clientId)
          .build()
      ).also { pixKeyReadingOneResponse: PixKeyReadingOneResponse ->
        assertEquals(key, pixKeyReadingOneResponse.key)
        assertEquals("ITAÚ UNIBANCO S.A.", pixKeyReadingOneResponse.account.institution)
        assertEquals(
          PixKeyReadingOneResponse.KeyType.RANDOM,
          pixKeyReadingOneResponse.keyType
        )
        assertEquals(clientId, pixKeyReadingOneResponse.clientId)
        assertEquals(pixId, pixKeyReadingOneResponse.pixId)
        assertEquals(
          PixKeyReadingOneResponse.Account.AccountType.CHECKING,
          pixKeyReadingOneResponse.account.accountType
        )
      }
    }

    @Test
    fun `should return details of the pix key when requesting by key`() {
      val key: String = UUID.randomUUID().toString()
      this@PixKeyReadingEndpointTests.pixKeyRepository.save(
        PixKey(
          type = PixKey.KeyType.RANDOM,
          key = key,
          clientId = UUID.randomUUID().toString(),
          accountType = PixKey.AccountType.CHECKING,
          participant = "60701190"
        )
      )

      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey(key)
          .build()
      ).also { pixKeyReadingOneResponse: PixKeyReadingOneResponse ->
        assertEquals(key, pixKeyReadingOneResponse.key)
        assertEquals("ITAÚ UNIBANCO S.A.", pixKeyReadingOneResponse.account.institution)
        assertEquals(
          PixKeyReadingOneResponse.KeyType.RANDOM,
          pixKeyReadingOneResponse.keyType
        )
        assertEquals("", pixKeyReadingOneResponse.clientId)
        assertEquals("", pixKeyReadingOneResponse.pixId)
        assertEquals(
          PixKeyReadingOneResponse.Account.AccountType.CHECKING,
          pixKeyReadingOneResponse.account.accountType
        )
      }
    }
  }

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

  @Test
  fun `should return UNAVAILABLE when a HttpClientException is thrown when connecting to the bcb one pix key reading service`() {
    assertThrows<StatusRuntimeException> {
      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey(this.mockBeanFactory.bcbReadOneReturnsHttpClientExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(Status.UNAVAILABLE)
    }
  }

  @Test
  fun `should return NOT_FOUND when a bcb one pix key reading service returns not found`() {
    assertThrows<StatusRuntimeException> {
      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey(this.mockBeanFactory.bcbReadOneReturnsNotFoundPixKey)
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(Status.NOT_FOUND)
    }
  }

  @Test
  fun `should return UNAVAILABLE when a bcb one pix key reading service returns an unknown response`() {
    assertThrows<StatusRuntimeException> {
      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey(this.mockBeanFactory.bcbReadOneReturnsUnknownResponsePixKey)
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(Status.UNAVAILABLE)
    }
  }

  @Test
  fun `should return INTERNAL when an unknown exception is thrown when connecting to the bcb one pix key reading service`() {
    assertThrows<StatusRuntimeException> {
      this@PixKeyReadingEndpointTests.grpcClient.readOnePixKey(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey(this.mockBeanFactory.bcbReadOneReturnsUnknownExceptionPixKey)
          .build()
      )
    }.also { statusRuntimeException ->
      statusRuntimeException.assertStatus(Status.INTERNAL)
    }
  }
}
