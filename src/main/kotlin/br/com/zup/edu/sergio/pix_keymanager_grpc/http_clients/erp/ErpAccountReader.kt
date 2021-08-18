package br.com.zup.edu.sergio.pix_keymanager_grpc.http_clients.erp

import br.com.zup.edu.sergio.pix_keymanager_grpc.isNotFound
import br.com.zup.edu.sergio.pix_keymanager_grpc.pixkey.creation.erpAccountType
import br.com.zup.edu.sergio.pix_keymanager_grpc.protobuf.PixKeyCreationRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErpAccountReader @Inject constructor(private val erpClient: ErpClient) {

  fun readAccount(
    pixKeyCreationRequest: PixKeyCreationRequest
  ): Single<DadosDaContaResponse> =
    this.erpClient
      .readAccount(
        clientId = pixKeyCreationRequest.clientId,
        accountType = pixKeyCreationRequest.erpAccountType()
      )
      .doOnError { error: Throwable ->
        if (error is HttpClientResponseException) {
          Single.error<DadosDaContaResponse>(translatedError(error = error))
        }
      }
}

private fun translatedError(
  error: HttpClientResponseException
): StatusRuntimeException {
  if (error.isNotFound()) {
    return Status.NOT_FOUND
      .withDescription("account not found")
      .augmentDescription(
        "can't confirm that an account exists within the erp system with " +
        "the client id and account type informed"
      )
      .asRuntimeException()
  }

  return Status.UNAVAILABLE
    .withDescription("account confirmation service unavailable")
    .augmentDescription(
      "can't confirm the account existence because the erp system is" +
      "returning an unknown response"
    )
    .asRuntimeException()
}
