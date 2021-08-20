package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PixKeyCreationRequestExtensionsTests {
  @Test
  fun `should throw AssertionError when trying to convert an invalid type to bcb KeyType`() {
    assertThrows<AssertionError> {
      PixKeyCreationRequest.getDefaultInstance().bcbKeyType
    }
  }

  @Test
  fun `should throw AssertionError when trying to convert an invalid accountType to a erp AccountType`() {
    assertThrows<AssertionError> {
      PixKeyCreationRequest.getDefaultInstance().erpAccountType
    }
  }
}
