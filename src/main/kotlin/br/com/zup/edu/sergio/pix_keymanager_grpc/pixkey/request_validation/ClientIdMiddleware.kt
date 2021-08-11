package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.PixKeyRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ExternalAccountType
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.client.exceptions.HttpClientResponseException

class ClientIdMiddleware(
  private val erpClient: ErpClient
) : PixKeyRequestMiddleware() {
  override fun check(pixKeyRequest: PixKeyRequest): StatusRuntimeException? =
    if (this.accountNotFound(pixKeyRequest)) {
      Status.NOT_FOUND
        .withDescription("account not found")
        .augmentDescription(
          """
            could not confirm that an account exists in the erp system with the 
            client id and account type informed
          """.trimIndent()
        )
        .asRuntimeException()
    } else {
      this.checkNext(pixKeyRequest)
    }

  private fun accountNotFound(pixKeyRequest: PixKeyRequest): Boolean =
    try {
      this.erpClient.readAccount(
        clientId = pixKeyRequest.clientId,
        accountType = ExternalAccountType.fromAccountType(pixKeyRequest.accountType)
      )
      false  // no exception thrown means account was found
    } catch (e: HttpClientResponseException) {
      true  // could not confirm that the account exists
    }

}
