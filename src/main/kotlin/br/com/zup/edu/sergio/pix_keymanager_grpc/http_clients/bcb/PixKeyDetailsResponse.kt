package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.StrParticipants
import br.com.zup.edu.sergio.pix_keymanager_grpc.asGoogleProtobufTimestamp
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyReadingOneResponse
import java.time.LocalDateTime

class PixKeyDetailsResponse(
  private val keyType: KeyType,
  val key: String,
  private val bankAccount: BankAccount,
  private val owner: Owner,
  private val createdAt: LocalDateTime
) {

  fun asPixKeyReadingOneResponseAccount(
    strParticipants: StrParticipants
  ): PixKeyReadingOneResponse.Account =
    PixKeyReadingOneResponse.Account.newBuilder()
      .setAccountType(this.bankAccount.pixKeyReadingOneResponseAccountType())
      .setBranch(this.bankAccount.branch)
      .setNumber(this.bankAccount.accountNumber)
      .setCreation(this.createdAt.asGoogleProtobufTimestamp())
      .setInstitution(
        this.bankAccount.participantName(strParticipants = strParticipants)
      )
      .setClient(this.owner.pixKeyReadingOneResponseAccountClient())
      .build()

  fun pixKeyReadingOneResponseKeyType(): PixKeyReadingOneResponse.KeyType =
    this.keyType.pixKeyReadingOneResponseKeyType()
}
