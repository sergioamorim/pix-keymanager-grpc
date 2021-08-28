package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.StrParticipants
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType

class BankAccount(
  val participant: String,
  val branch: String,
  val accountNumber: String,
  val accountType: AccountType
) {
  fun modelAccountType(): PixKey.AccountType = this.accountType.modelAccountType()

  fun protobufAccountType(): ProtobufAccountType =
    this.accountType.protobufAccountType()

  fun participantName(strParticipants: StrParticipants): String =
    strParticipants.nameOf(this.participant)
}
