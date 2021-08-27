package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.StrParticipants
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse.Account

class BankAccount(
  val participant: String,
  val branch: String,
  val accountNumber: String,
  val accountType: AccountType
) {
  fun modelAccountType(): PixKey.AccountType = this.accountType.modelAccountType()

  fun pixKeyReadingOneResponseAccountType(): Account.AccountType =
    this.accountType.pixKeyReadingOneResponseAccountType()

  fun participantName(strParticipants: StrParticipants): String =
    strParticipants.nameOf(this.participant)
}
