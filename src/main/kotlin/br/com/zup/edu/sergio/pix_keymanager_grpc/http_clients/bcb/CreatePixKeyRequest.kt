package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.bcb

import br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp.DadosDaContaResponse
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.bcbKeyType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest

class CreatePixKeyRequest(
  pixKeyCreationRequest: PixKeyCreationRequest,
  dadosDaContaResponse: DadosDaContaResponse
) {
  val keyType: KeyType = pixKeyCreationRequest.bcbKeyType
  val key: String = pixKeyCreationRequest.key
  val bankAccount: BankAccount = dadosDaContaResponse.bcbBankAccount
  val owner: Owner = dadosDaContaResponse.bcbOwner
}
