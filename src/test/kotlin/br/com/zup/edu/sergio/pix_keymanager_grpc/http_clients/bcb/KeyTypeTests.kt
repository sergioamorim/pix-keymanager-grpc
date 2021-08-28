package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

class KeyTypeTests {
  @Test
  fun `should be able to convert itself when CPF to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(ProtobufKeyType.KEY_TYPE_CPF, KeyType.CPF.protobufKeyType())
  }

  @Test
  fun `should be able to convert itself when EMAIL to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(ProtobufKeyType.KEY_TYPE_EMAIL, KeyType.EMAIL.protobufKeyType())
  }

  @Test
  fun `should be able to convert itself when PHONE to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(ProtobufKeyType.KEY_TYPE_PHONE, KeyType.PHONE.protobufKeyType())
  }

  @Test
  fun `should be able to convert itself when RANDOM to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(ProtobufKeyType.KEY_TYPE_RANDOM, KeyType.RANDOM.protobufKeyType())
  }

  @Test
  fun `should be able to convert itself when CNPJ to KeyType from PixKeyReadingOneResponse`() {
    assertEquals(ProtobufKeyType.KEY_TYPE_CNPJ, KeyType.CNPJ.protobufKeyType())
  }
}
