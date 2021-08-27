package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.PixKey
import java.time.LocalDateTime

class CreatePixKeyResponse(
  private val keyType: KeyType,
  private val key: String,
  private val bankAccount: BankAccount,
  private val owner: Owner,
  private val createdAt: LocalDateTime
) {

  fun asPixKey(clientId: String): PixKey {
    return PixKey(
      type = this.keyType.modelKeyType(),
      key = this.key,
      clientId = clientId,
      accountType = this.bankAccount.modelAccountType(),
      participant = this.bankAccount.participant
    )
  }
}
