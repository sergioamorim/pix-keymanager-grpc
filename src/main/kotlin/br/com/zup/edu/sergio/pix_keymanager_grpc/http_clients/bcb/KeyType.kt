package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

enum class KeyType {
  CPF, CNPJ, PHONE, EMAIL, RANDOM;

  fun modelKeyType(): PixKey.KeyType =
    when (this) {
      CPF -> PixKey.KeyType.CPF
      EMAIL -> PixKey.KeyType.EMAIL
      PHONE -> PixKey.KeyType.PHONE
      RANDOM -> PixKey.KeyType.RANDOM
      CNPJ -> PixKey.KeyType.CNPJ
    }

  fun protobufKeyType(): ProtobufKeyType =
    when (this) {
      CPF -> ProtobufKeyType.KEY_TYPE_CPF
      EMAIL -> ProtobufKeyType.KEY_TYPE_EMAIL
      PHONE -> ProtobufKeyType.KEY_TYPE_PHONE
      RANDOM -> ProtobufKeyType.KEY_TYPE_RANDOM
      CNPJ -> ProtobufKeyType.KEY_TYPE_CNPJ
    }
}
