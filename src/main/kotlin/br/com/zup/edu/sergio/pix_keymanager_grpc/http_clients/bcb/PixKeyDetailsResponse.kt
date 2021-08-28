package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.StrParticipants
import br.com.zup.edu.sergio.pix_keymanager_grpc.googleProtobufTimestamp
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import java.time.LocalDateTime
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.AccountType as ProtobufAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.KeyType as ProtobufKeyType

class PixKeyDetailsResponse(
  private val keyType: KeyType,
  val key: String,
  private val bankAccount: BankAccount,
  private val owner: Owner,
  val createdAt: LocalDateTime
) {

  fun asPixKeyReadingOneResponseAccount(
    strParticipants: StrParticipants
  ): PixKeyReadingOneResponse.Account =
    PixKeyReadingOneResponse.Account.newBuilder()
      .setAccountType(this.bankAccount.protobufAccountType())
      .setBranch(this.bankAccount.branch)
      .setNumber(this.bankAccount.accountNumber)
      .setCreation(this.createdAt.googleProtobufTimestamp)
      .setInstitution(
        this.bankAccount.participantName(strParticipants = strParticipants)
      )
      .setClient(this.owner.pixKeyReadingOneResponseAccountClient())
      .build()

  val protobufKeyType: ProtobufKeyType
    get() = this.keyType.protobufKeyType()

  val protobufAccountType: ProtobufAccountType
    get() = this.bankAccount.protobufAccountType()
}
