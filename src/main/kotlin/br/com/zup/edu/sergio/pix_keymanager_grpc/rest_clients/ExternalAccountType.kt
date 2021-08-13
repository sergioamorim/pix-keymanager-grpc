package br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients

import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest

enum class ExternalAccountType {
  CONTA_CORRENTE, CONTA_POUPANCA;

  companion object {
    fun fromPixKeyCreationRequestAccountType(
      accountType: PixKeyCreationRequest.AccountType
    ): ExternalAccountType? =
      when (accountType) {
        PixKeyCreationRequest.AccountType.CHECKING -> CONTA_CORRENTE
        PixKeyCreationRequest.AccountType.SAVINGS -> CONTA_POUPANCA
        else -> null
      }
  }
}
