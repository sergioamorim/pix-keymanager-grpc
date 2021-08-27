package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse.Account

enum class AccountType {
  CACC, SVGS;

  fun modelAccountType(): PixKey.AccountType =
    when (this) {
      CACC -> PixKey.AccountType.CHECKING
      SVGS -> PixKey.AccountType.SAVINGS
    }

  fun pixKeyReadingOneResponseAccountType(): Account.AccountType =
    when (this) {
      CACC -> Account.AccountType.CHECKING
      SVGS -> Account.AccountType.SAVINGS
    }
}
