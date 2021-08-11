package br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients

import br.com.zup.edu.sergio.pix_keymanager_grpc.AccountType

enum class ExternalAccountType {
  CONTA_CORRENTE, CONTA_POUPANCA;

  companion object {
    fun fromAccountType(accountType: AccountType): ExternalAccountType? =
      when (accountType) {
        AccountType.CHECKING -> CONTA_CORRENTE
        AccountType.SAVINGS -> CONTA_POUPANCA
        else -> null
      }
  }
}
