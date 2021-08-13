package br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.request_validation

import br.com.zup.edu.sergio.pix_keymanager_grpc.RequestMiddleware
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ErpClient
import br.com.zup.edu.sergio.pix_keymanager_grpc.rest_clients.ExternalAccountType
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException

class ClientIdMiddleware(
  private val erpClient: ErpClient
) : RequestMiddleware<PixKeyCreationRequest>() {
  override fun check(request: PixKeyCreationRequest): StatusRuntimeException? =
    when (this.accountConfirmationStatus(request)) {

      HttpStatus.OK -> this.checkNext(request)

      HttpStatus.NOT_FOUND -> Status.NOT_FOUND
        .withDescription("account not found")
        .augmentDescription(
          """
            could not confirm that an account exists in the erp system with the 
            client id and account type informed
          """.trimIndent()
        )
        .asRuntimeException()

      else -> Status.UNAVAILABLE
        .withDescription("account confirmation service unavailable")
        .augmentDescription(
          """
            can't confirm the account existence because the erp system is not 
            returning a known response
          """.trimIndent()
        )
        .asRuntimeException()
    }

  private fun accountConfirmationStatus(
    pixKeyCreationRequest: PixKeyCreationRequest
  ): HttpStatus =
    try {
      this.erpClient.readAccount(
        clientId = pixKeyCreationRequest.clientId,
        accountType = ExternalAccountType.fromPixKeyCreationRequestAccountType(
          pixKeyCreationRequest.accountType
        )
      )
      HttpStatus.OK
    } catch (httpClientResponseException: HttpClientResponseException) {
      httpClientResponseException.status
    }

}
