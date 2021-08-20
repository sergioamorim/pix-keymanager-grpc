package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class DeletePixKeyRequestTests {
  @Test
  fun `should have key and participant String properties`() {
    val key: String = UUID.randomUUID().toString()
    val participant = "60701190"

    DeletePixKeyRequest(key = key, participant = participant)
      .also { deletePixKeyRequest: DeletePixKeyRequest ->
        assertEquals(key, deletePixKeyRequest.key)
        assertEquals(participant, deletePixKeyRequest.participant)
      }
  }
}
