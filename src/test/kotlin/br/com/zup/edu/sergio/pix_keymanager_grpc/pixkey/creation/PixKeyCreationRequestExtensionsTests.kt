package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PixKeyCreationRequestExtensionsTests {

  @Nested
  inner class AsPixKeyTests {
    @Test
    @DisplayName("Should return an equivalent PixKey when type and accountType are valid")
    fun shouldReturnAnEquivalentPixKeyWhenTypeAndAccountTypeAreValid() {
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
    @DisplayName("Should throw assertion error when is called with an invalid key type")
    fun shouldThrowAssertionErrorWhenIsCalledWithAnInvalidKeyType() {
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
    @DisplayName("Should throw assertion error when is called with an invalid account type")
    fun shouldThrowAssertionErrorWhenIsCalledWithAnInvalidAccountType() {
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