package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType

enum class AccountType {
  CACC, SVGS;

  fun modelAccountType(): PixKey.AccountType =
    when (this) {
      CACC -> PixKey.AccountType.CHECKING
      SVGS -> PixKey.AccountType.SAVINGS
    }

  fun protobufAccountType(): ProtobufAccountType =
    when (this) {
      CACC -> ProtobufAccountType.ACCOUNT_TYPE_CHECKING
      SVGS -> ProtobufAccountType.ACCOUNT_TYPE_SAVINGS
    }
}
