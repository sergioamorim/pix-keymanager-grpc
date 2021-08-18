package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey

class BankAccount(
  val participant: String,
  val branch: String,
  val accountNumber: String,
  val accountType: AccountType
) {
  fun modelAccountType(): PixKey.AccountType =
    when (this.accountType) {
      AccountType.CACC -> PixKey.AccountType.CHECKING
      AccountType.SVGS -> PixKey.AccountType.SAVINGS
    }
}
