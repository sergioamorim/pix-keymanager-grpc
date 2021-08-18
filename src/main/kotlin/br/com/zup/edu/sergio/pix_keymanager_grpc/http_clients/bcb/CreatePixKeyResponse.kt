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

  private val modelKeyType: PixKey.KeyType =
    when (this.keyType) {
      KeyType.CPF -> PixKey.KeyType.CPF
      KeyType.EMAIL -> PixKey.KeyType.EMAIL
      KeyType.PHONE -> PixKey.KeyType.PHONE_NUMBER
      KeyType.RANDOM -> PixKey.KeyType.RANDOM
      KeyType.CNPJ -> PixKey.KeyType.CNPJ
    }

  fun asPixKey(clientId: String): PixKey {
    return PixKey(
      type = this.modelKeyType,
      key = this.key,
      clientId = clientId,
      accountType = this.bankAccount.modelAccountType(),
      participant = this.bankAccount.participant
    )
  }
}
