package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse

enum class KeyType {
  CPF, CNPJ, PHONE, EMAIL, RANDOM;

  fun modelKeyType(): PixKey.KeyType =
    when (this) {
      CPF -> PixKey.KeyType.CPF
      EMAIL -> PixKey.KeyType.EMAIL
      PHONE -> PixKey.KeyType.PHONE_NUMBER
      RANDOM -> PixKey.KeyType.RANDOM
      CNPJ -> PixKey.KeyType.CNPJ
    }

  fun pixKeyReadingOneResponseKeyType(): PixKeyReadingOneResponse.KeyType =
    when (this) {
      CPF -> PixKeyReadingOneResponse.KeyType.CPF
      EMAIL -> PixKeyReadingOneResponse.KeyType.EMAIL
      PHONE -> PixKeyReadingOneResponse.KeyType.PHONE_NUMBER
      RANDOM -> PixKeyReadingOneResponse.KeyType.RANDOM
      CNPJ -> PixKeyReadingOneResponse.KeyType.CNPJ
    }
}
