package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PixKeyCreationRequestExtensionsTests {

  @Nested
  inner class AsPixKeyTests {
    @Test
    fun `should return an equivalent PixKey when type and accountType are valid`() {
      val key = "12345678901"
      val clientId = "client id"

      with(
        PixKeyCreationRequest
          .newBuilder()
          .setType(PixKeyCreationRequest.KeyType.CPF)
          .setKey(key)
          .setClientId(clientId)
          .setAccountType(PixKeyCreationRequest.AccountType.CHECKING)
          .build()
          .asPixKey()
      ) {
        assertEquals(PixKey.KeyType.CPF, this.type)
        assertEquals(key, this.key)
        assertEquals(clientId, this.clientId)
        assertEquals(PixKey.AccountType.CHECKING, this.accountType)
      }
    }

    @Test
    fun `should throw assertion error when called with an invalid key type`() {
      assertThrows<AssertionError> {
        PixKeyCreationRequest
          .newBuilder()
          .setKey("12345678901")
          .setClientId("client id")
          .setAccountType(PixKeyCreationRequest.AccountType.CHECKING)
          .build()
          .asPixKey()
      }
    }

    @Test
    fun `should throw assertion error when called with an invalid account type`() {
      assertThrows<AssertionError> {
        PixKeyCreationRequest
          .newBuilder()
          .setType(PixKeyCreationRequest.KeyType.CPF)
          .setKey("12345678901")
          .setClientId("client id")
          .build()
          .asPixKey()
      }
    }
  }
}