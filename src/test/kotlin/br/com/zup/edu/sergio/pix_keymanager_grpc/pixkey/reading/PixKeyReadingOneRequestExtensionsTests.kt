package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.reading

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneRequest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PixKeyReadingOneRequestExtensionsTests {
  @Nested
  inner class IsLocalTests {
    @Test
    fun `should return true when both pixId and clientId are not blank and the pixKey is blank`() {
      assertTrue(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("lorem")
          .setClientId("ipsum")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when both pixId and clientId are not blank but the pixKey is also not blank`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("dolor")
          .setClientId("sit")
          .setPixKey("amet")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when the pixId is not blank, the pixKey is blank but so is the clientId`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("sed")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when the clientId is not blank, the pixKey is blank but so is the pixId`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setClientId("ex")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when the pixKey is blank but so is both the pixId and the pixKey`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when both the pixId and the clientId are not blank and the pixKey is not blank`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey("in")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when the pixId is not blank but the clientId is and the pixKey is not`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("id")
          .setPixKey("eu")
          .build()
          .isLocal
      )
    }

    @Test
    fun `should return false when the clientId is not blank but the pixId is and the pixKey is not`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setClientId("id")
          .setPixKey("eu")
          .build()
          .isLocal
      )
    }
  }

  @Nested
  inner class IsExternalTests {
    @Test
    fun `should return false when both pixId and clientId are not blank and the pixKey is blank`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("lorem")
          .setClientId("ipsum")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when both pixId and clientId are not blank but the pixKey is also not blank`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("dolor")
          .setClientId("sit")
          .setPixKey("amet")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when the pixId is not blank, the pixKey is blank but so is the clientId`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("sed")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when the clientId is not blank, the pixKey is blank but so is the pixId`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setClientId("ex")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when the pixKey is blank but so is both the pixId and the pixKey`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return true when both the pixId and the clientId are not blank and the pixKey is not blank`() {
      assertTrue(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixKey("in")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when the pixId is not blank but the clientId is and the pixKey is not`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setPixId("id")
          .setPixKey("eu")
          .build()
          .isExternal
      )
    }

    @Test
    fun `should return false when the clientId is not blank but the pixId is and the pixKey is not`() {
      assertFalse(
        PixKeyReadingOneRequest
          .newBuilder()
          .setClientId("id")
          .setPixKey("eu")
          .build()
          .isExternal
      )
    }
  }
}
