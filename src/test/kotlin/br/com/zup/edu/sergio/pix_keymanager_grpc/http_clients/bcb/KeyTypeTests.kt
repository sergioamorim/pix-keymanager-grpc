package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KeyTypeTests {
  @Test
  fun `should be able to convert itself when CPF to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(
      PixKeyReadingOneResponse.KeyType.CPF,
      KeyType.CPF.pixKeyReadingOneResponseKeyType()
    )
  }

  @Test
  fun `should be able to convert itself when EMAIL to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(
      PixKeyReadingOneResponse.KeyType.EMAIL,
      KeyType.EMAIL.pixKeyReadingOneResponseKeyType()
    )
  }

  @Test
  fun `should be able to convert itself when PHONE to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(
      PixKeyReadingOneResponse.KeyType.PHONE_NUMBER,
      KeyType.PHONE.pixKeyReadingOneResponseKeyType()
    )
  }

  @Test
  fun `should be able to convert itself when RANDOM to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(
      PixKeyReadingOneResponse.KeyType.RANDOM,
      KeyType.RANDOM.pixKeyReadingOneResponseKeyType()
    )
  }

  @Test
  fun `should be able to convert itself when CNPJ to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(
      PixKeyReadingOneResponse.KeyType.CNPJ,
      KeyType.CNPJ.pixKeyReadingOneResponseKeyType()
    )
  }
}
